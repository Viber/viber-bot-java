package com.viber.bot;

import com.google.common.util.concurrent.ListenableFuture;
import com.viber.bot.api.ApiException;
import com.viber.bot.api.ViberBot;
import com.viber.bot.message.Message;
import com.viber.bot.message.TextMessage;
import com.viber.bot.profile.UserProfile;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;

@Immutable
public class Response {

    private final UserProfile to;
    private final ViberBot bot;

    public Response(final UserProfile to, final ViberBot bot) {
        this.to = to;
        this.bot = bot;
    }

    /**
     * Shorthand for {@link ViberBot#sendMessage(UserProfile, Collection)}
     * Where the {@link UserProfile} is already set.
     *
     * @param messages collection of messages to be sent
     * @return future which contains a collection of message tokens as strings, that may throw {@link ApiException}.
     * @see ViberBot#sendMessage(UserProfile, Collection)
     */
    public ListenableFuture<Collection<String>> send(final @Nonnull Collection<Message> messages) {
        return bot.sendMessage(to, messages);
    }

    /**
     * Shorthand for {@link ViberBot#sendMessage(UserProfile, Message...)}
     * Where the {@link UserProfile} is already set.
     *
     * @param messages collection of messages to be sent
     * @return future which contains a collection of message tokens as strings, that may throw {@link ApiException}.
     * @see ViberBot#sendMessage(UserProfile, Message...)
     */
    public ListenableFuture<Collection<String>> send(final @Nonnull Message... messages) {
        return bot.sendMessage(to, messages);
    }

    /**
     * Shorthand for {@link ViberBot#sendMessage(UserProfile, Message...)}
     * The method will create a {@link TextMessage} for you with the String provided to it.
     *
     * @param textMessage string to be as text for the message
     * @return future which contains a collection of message tokens as strings, that may throw {@link ApiException}.
     * @see ViberBot#sendMessage(UserProfile, Message...)
     */
    public ListenableFuture<Collection<String>> send(final @Nonnull String textMessage) {
        return bot.sendMessage(to, new TextMessage(textMessage));
    }

}
