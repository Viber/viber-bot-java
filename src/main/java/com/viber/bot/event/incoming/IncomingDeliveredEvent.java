package com.viber.bot.event.incoming;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viber.bot.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.viber.bot.Preconditions.checkNotEmpty;

@Immutable
public class IncomingDeliveredEvent extends IncomingEvent {

    private final String userId;
    private final Long token;

    @JsonCreator
    IncomingDeliveredEvent(final @JsonProperty("user_id") @Nonnull String userId,
                           final @JsonProperty("message_token") long token,
                           final @JsonProperty("timestamp") long timestamp) {
        super(Event.MESSAGE_DELIVERED, timestamp);
        this.userId = checkNotEmpty(userId);
        this.token = token;
    }

    public Long getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final IncomingDeliveredEvent that = (IncomingDeliveredEvent) o;

        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return token != null ? token.equals(that.token) : that.token == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }
}
