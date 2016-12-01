package com.viber.bot.event.callback;

import com.viber.bot.event.BotEventListener;
import com.viber.bot.event.incoming.IncomingConversationStartedEvent;
import com.viber.bot.message.Message;

import java.util.Optional;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkArgument;

public interface OnConversationStarted extends BotEventListener<Optional<Message>> {
    Future<Optional<Message>> conversationStarted(IncomingConversationStartedEvent event);

    @Override
    default Future<Optional<Message>> emit(final Object... args) {
        checkArgument(args.length == 1);
        return conversationStarted((IncomingConversationStartedEvent) args[0]);
    }
}
