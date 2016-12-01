package com.viber.bot.event.callback;

import com.viber.bot.Response;
import com.viber.bot.event.BotEventListener;
import com.viber.bot.event.incoming.IncomingSubscribedEvent;

import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkArgument;

public interface OnSubscribe extends BotEventListener<Void> {
    void subscribe(IncomingSubscribedEvent event, Response response);

    @Override
    default Future<Void> emit(final Object... args) {
        checkArgument(args.length == 2);
        subscribe((IncomingSubscribedEvent) args[0], (Response) args[1]);
        return nothing;
    }
}