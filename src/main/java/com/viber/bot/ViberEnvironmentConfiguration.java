package com.viber.bot;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import javax.annotation.Nullable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class ViberEnvironmentConfiguration {

    private static final String CONFIG_PREFIX = "com.viber.bot";
    private static final String CONFIG_EXECUTOR_SERVICE_STRATEGY = "executor.strategy";
    private static final String CONFIG_NUMBER_OF_THREADS = "executor.threads";

    private static final AtomicReference<ListeningExecutorService> executorService = new AtomicReference<>(null);

    public static ListeningExecutorService getExecutorService() {
        if (getExecutorServiceStrategy() == ExecutorServiceStrategy.DIRECT) {
            return MoreExecutors.newDirectExecutorService();
        } else {
            executorService.compareAndSet(null, MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(getNumberOfThreads())));
            return executorService.get();
        }
    }

    private static int getNumberOfThreads() {
        return getPropertyOrDefault(CONFIG_NUMBER_OF_THREADS, Integer::valueOf, Runtime.getRuntime().availableProcessors());
    }

    private static ExecutorServiceStrategy getExecutorServiceStrategy() {
        return getPropertyOrDefault(CONFIG_EXECUTOR_SERVICE_STRATEGY, ExecutorServiceStrategy::valueOf, ExecutorServiceStrategy.DIRECT);
    }

    private static <T> T getPropertyOrDefault(final String suffix, final Function<String, T> compose, final @Nullable T def) {
        final String configProperty = String.format("%s.%s", CONFIG_PREFIX, suffix);
        final String configValue = System.getProperty(configProperty);
        return Strings.isNullOrEmpty(configValue) ? def : compose.apply(configValue.toUpperCase());
    }

    private enum ExecutorServiceStrategy {
        DIRECT, THREAD
    }
}
