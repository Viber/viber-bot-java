package com.viber.bot.middleware;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.viber.bot.Request;
import com.viber.bot.ViberEnvironmentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.io.InputStream;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@ThreadSafe
class PubSubMiddleware extends AbstractScheduledService implements Middleware {

    private static final Logger logger = LoggerFactory.getLogger(PubSubMiddleware.class);

    private static final int CYCLE_MINUTES = 1;
    private static final boolean SHOULD_RECOVER = true;

    private final ExecutorService pollingThreadPool = Executors.newSingleThreadExecutor();
    private final ListeningExecutorService ioThreadPool = ViberEnvironmentConfiguration.getExecutorService();
    private final TransferQueue<Request> transferQueue = new LinkedTransferQueue<>();
    private final RequestReceiver requestReceiver;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final Stats stats = new Stats();

    PubSubMiddleware(final @Nonnull RequestReceiver requestReceiver) {
        this.requestReceiver = checkNotNull(requestReceiver);
        startPolling();
        startAsync();
    }

    @Override
    public ListenableFuture<InputStream> incoming(final @Nonnull Request request) {
        try {
            transferQueue.transfer(request);
        } catch (final InterruptedException e) {
            Throwables.throwIfUnchecked(e);
        }

        return ioThreadPool.submit(() -> {
            waitOnRequestToFinish(request);
            return request.getResponseInputStream();
        });
    }

    private void startPolling() {
        checkState(isRunning.compareAndSet(false, true), "already running");
        try {
            logger.info("Started polling from queue..");
            pollingThreadPool.execute(() -> requestLoop());
        } catch (final Exception e) {
            logger.error("Exception from request receiver, restarting polling", e);
            restartPolling();
        }
    }

    private void restartPolling() {
        checkState(isRunning.compareAndSet(true, false), "not running");
        checkState(!pollingThreadPool.isTerminated(), "executor is already terminated, cannot reset");
        checkState(SHOULD_RECOVER, "could not restart polling, not allowed to recover");
        startPolling();
    }

    private void requestLoop() {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        while (isRunning.get()) {
            stopwatch.reset().start();
            acceptNextRequest();
            stats.requestsLatency.addAndGet(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    private void acceptNextRequest() {
        try (Request request = transferQueue.take()) {
            requestReceiver.acceptRequest(request);
            stats.requestsPerCycle.incrementAndGet();
        } catch (final Exception e) {
            logger.error("Exception during event loop", e);
            stats.exceptionsPerCycle.incrementAndGet();
        }
    }

    private void waitOnRequestToFinish(final @Nonnull Request request) {
        synchronized (request) {
            try {
                if (!request.isClosed()) request.wait();
            } catch (final InterruptedException e) {
                logger.error("Interrupted during request", e);
            }
        }
    }

    @Override
    protected void runOneIteration() throws Exception {
        final double avgLatency = (stats.getRequestsLatency() / Math.max(stats.getRequestsPerCycle(), 1)) / 1000;
        logger.debug("Requests: {}, Exceptions: {}, Avg latency: {}s, Queue size: {}",
                stats.getRequestsPerCycle(), stats.getExceptionsPerCycle(), Math.round(avgLatency), transferQueue.size());
        stats.reset();
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, CYCLE_MINUTES, TimeUnit.MINUTES);
    }

    private static class Stats {
        private final AtomicInteger requestsPerCycle = new AtomicInteger(0);
        private final AtomicInteger exceptionsPerCycle = new AtomicInteger(0);
        private final AtomicDouble requestsLatency = new AtomicDouble(0D);

        private void reset() {
            requestsPerCycle.set(0);
            exceptionsPerCycle.set(0);
            requestsLatency.set(0);
        }

        int getRequestsPerCycle() {
            return requestsPerCycle.get();
        }

        int getExceptionsPerCycle() {
            return exceptionsPerCycle.get();
        }

        double getRequestsLatency() {
            return requestsLatency.get();
        }
    }
}
