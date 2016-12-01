package com.viber.bot.event.callback;

import com.viber.bot.event.BotEventListener;
import com.viber.bot.event.incoming.IncomingUnsubscribeEvent;

import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkArgument;

public interface OnUnsubscribe extends BotEventListener<Void> {
    void unsubscribe(IncomingUnsubscribeEvent event);

    @Override
    default Future<Void> emit(final Object... args) {
        checkArgument(args.length == 1);
        unsubscribe((IncomingUnsubscribeEvent) args[0]);
        return nothing;
    }
}