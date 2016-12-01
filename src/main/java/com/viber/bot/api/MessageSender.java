package com.viber.bot.api;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.viber.bot.message.Message;
import com.viber.bot.profile.BotProfile;
import com.viber.bot.profile.UserProfile;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

class MessageSender {
    private static final String MESSAGE_TOKEN = "message_token";

    private final BotProfile botProfile;
    private final ViberClient client;

    MessageSender(final @Nonnull BotProfile botProfile, final @Nonnull ViberClient client) {
        this.botProfile = checkNotNull(botProfile);
        this.client = checkNotNull(client);
    }

    ListenableFuture<Collection<String>> sendMessage(final @Nonnull UserProfile to, final @Nonnull Collection<Message> messages) {
        final Collection<String> messageTokens = Lists.newArrayList();
        final Iterator<Message> iterator = messages.iterator();

        while (iterator.hasNext()) {
            final Message message = iterator.next();

            if (!iterator.hasNext()) {
                messageTokens.add(Futures.getUnchecked(Futures.transform(sendMessageWithKeyboard(to, message), getMessageToken())));
            } else {
                messageTokens.add(Futures.getUnchecked(Futures.transform(sendMessageWithoutKeyboard(to, message), getMessageToken())));
            }
        }
        return Futures.immediateFuture(messageTokens);
    }

    private ListenableFuture<ApiResponse> sendMessageWithoutKeyboard(final UserProfile to, final Message message) {
        return client.sendMessage(botProfile, to, message, Optional.empty(), Optional.of(message.getTrackingData()));
    }

    private ListenableFuture<ApiResponse> sendMessageWithKeyboard(final UserProfile to, final Message message) {
        return client.sendMessage(botProfile, to, message, Optional.of(message.getKeyboard()), Optional.of(message.getTrackingData()));
    }

    private Function<ApiResponse, String> getMessageToken() {
        return response -> response.get(MESSAGE_TOKEN).toString();
    }
}
