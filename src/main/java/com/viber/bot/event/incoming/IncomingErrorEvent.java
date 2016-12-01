package com.viber.bot.event.incoming;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viber.bot.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.viber.bot.Preconditions.checkNotEmpty;

@Immutable
public class IncomingErrorEvent extends IncomingEvent {

    private final String userId;
    private final String description;
    private final Long token;

    @JsonCreator
    IncomingErrorEvent(final @JsonProperty("user_id") @Nonnull String userId,
                       final @JsonProperty("desc") @Nonnull String description,
                       final @JsonProperty("message_token") long token,
                       final @JsonProperty("timestamp") long timestamp) {
        super(Event.ERROR, timestamp);
        this.userId = checkNotEmpty(userId);
        this.description = checkNotEmpty(description);
        this.token = token;
    }

    public String getDescription() {
        return description;
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

        final IncomingErrorEvent that = (IncomingErrorEvent) o;

        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        return token != null ? token.equals(that.token) : that.token == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }
}
