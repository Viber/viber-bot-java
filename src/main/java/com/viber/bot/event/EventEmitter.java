package com.viber.bot.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventEmitter {

    private final Multimap<Event, EmittableEvent> listeners = ArrayListMultimap.create();

    public void on(final @Nonnull Event event, final @Nonnull EmittableEvent listener) {
        listeners.put(checkNotNull(event), checkNotNull(listener));
    }

    public <T> Collection<Future<T>> emit(final @Nonnull Event event, final Object... args) {
        final Collection<Future<T>> futures = Lists.newArrayList();
        listeners.get(event).forEach(listener -> futures.add(listener.emit(args)));
        return futures;
    }

    public interface EmittableEvent<T> {
        Future<T> emit(Object... args);
    }
}
