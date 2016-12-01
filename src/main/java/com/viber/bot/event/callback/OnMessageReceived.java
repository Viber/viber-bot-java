package com.viber.bot.event.callback;

import com.viber.bot.Response;
import com.viber.bot.event.BotEventListener;
import com.viber.bot.event.incoming.IncomingMessageEvent;
import com.viber.bot.message.Message;

import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkArgument;

public interface OnMessageReceived extends BotEventListener<Void> {
    void messageReceived(IncomingMessageEvent event, Message message, Response response);

    @Override
    default Future<Void> emit(final Object... args) {
        checkArgument(args.length == 3);
        messageReceived((IncomingMessageEvent) args[0], (Message) args[1], (Response) args[2]);
        return nothing;
    }
}