package com.viber.bot.event;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class EventEmitterTest {

    private final EventEmitter eventEmitter = new EventEmitter();

    @Test
    public void testEventEmitterSanity() {
        final AtomicBoolean isEventReceived = new AtomicBoolean(false);
        eventEmitter.on(Event.ERROR, args -> {
            isEventReceived.compareAndSet(false, true);
            return null;
        });

        eventEmitter.emit(Event.ERROR);
        await().untilAtomic(isEventReceived, equalTo(true));
    }

    @Test
    public void testEventEmitterMultipleEmitsInOrder() throws ExecutionException, InterruptedException {
        final int nTests = 10;
        final AtomicInteger numberOfEvents = new AtomicInteger(0);

        eventEmitter.on(Event.WEBHOOK, args -> {
            numberOfEvents.incrementAndGet();
            return Futures.immediateFuture(args[0]);
        });

        final List<Future<Integer>> futures = Lists.newArrayList();
        IntStream.range(0, nTests).forEach(i -> futures.addAll(eventEmitter.emit(Event.WEBHOOK, i)));
        await().untilAtomic(numberOfEvents, equalTo(nTests));

        for (int i = 0; i < nTests; i++) {
            assertThat(futures.get(i).get()).isEqualTo(i);
        }
    }

    @Test
    public void testEventEmitterMultipleListenersInOrder() throws ExecutionException, InterruptedException {
        final int nTests = 10;
        final AtomicInteger numberOfEvents = new AtomicInteger(0);

        eventEmitter.on(Event.WEBHOOK, args -> {
            numberOfEvents.incrementAndGet();
            return Futures.immediateFuture("a_" + args[0]);
        });

        eventEmitter.on(Event.WEBHOOK, args -> {
            numberOfEvents.incrementAndGet();
            return Futures.immediateFuture("b_" + args[0]);
        });

        final List<Future<String>> futures = Lists.newArrayList();
        IntStream.range(0, nTests).forEach(i -> futures.addAll(eventEmitter.emit(Event.WEBHOOK, i)));
        await().untilAtomic(numberOfEvents, equalTo(nTests * 2));

        for (int i = 0; i < nTests; i += 2) {
            final int expected = i / 2;
            assertThat(futures.get(i).get()).isEqualTo("a_" + expected);
            assertThat(futures.get(i + 1).get()).isEqualTo("b_" + expected);
        }
    }
}