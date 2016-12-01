package com.viber.bot.event.callback;

import com.viber.bot.event.BotEventListener;
import com.viber.bot.event.incoming.IncomingSeenEvent;

import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkArgument;

public interface OnMessageSeen extends BotEventListener<Void> {
    void messageSeen(IncomingSeenEvent event);

    @Override
    default Future<Void> emit(final Object... args) {
        checkArgument(args.length == 1);
        messageSeen((IncomingSeenEvent) args[0]);
        return nothing;
    }
}
