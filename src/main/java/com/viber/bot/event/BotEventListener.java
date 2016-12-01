package com.viber.bot.event;

import com.google.common.util.concurrent.Futures;

import java.util.EventListener;
import java.util.concurrent.Future;

public interface BotEventListener<T> extends EventListener, EventEmitter.EmittableEvent<T> {
    Future<Void> nothing = Futures.immediateFuture(null);
}
