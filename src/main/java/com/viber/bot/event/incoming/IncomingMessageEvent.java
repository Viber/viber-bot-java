package com.viber.bot.event.incoming;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viber.bot.event.Event;
import com.viber.bot.message.Message;
import com.viber.bot.profile.UserProfile;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

@Immutable
public class IncomingMessageEvent extends IncomingEvent {

    private final Long token;
    private final Message message;
    private final UserProfile sender;

    @JsonCreator
    IncomingMessageEvent(final @JsonProperty("message") @Nonnull Message message,
                         final @JsonProperty("sender") @Nonnull UserProfile sender,
                         final @JsonProperty("message_token") long token,
                         final @JsonProperty("timestamp") long timestamp) {
        super(Event.MESSAGE_RECEIVED, timestamp);
        this.message = checkNotNull(message);
        this.sender = checkNotNull(sender);
        this.token = token;
    }

    public Long getToken() {
        return token;
    }

    public Message getMessage() {
        return message;
    }

    public UserProfile getSender() {
        return sender;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final IncomingMessageEvent that = (IncomingMessageEvent) o;

        if (token != null ? !token.equals(that.token) : that.token != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (sender != null ? !sender.equals(that.sender) : that.sender != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (sender != null ? sender.hashCode() : 0);
        return result;
    }
}
