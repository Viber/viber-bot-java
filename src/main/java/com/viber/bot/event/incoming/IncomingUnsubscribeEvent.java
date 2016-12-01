package com.viber.bot.event.incoming;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viber.bot.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.viber.bot.Preconditions.checkNotEmpty;

@Immutable
public class IncomingUnsubscribeEvent extends IncomingEvent {

    private final String userId;

    @JsonCreator
    IncomingUnsubscribeEvent(final @JsonProperty("user_id") @Nonnull String userId,
                             final @JsonProperty("timestamp") long timestamp) {
        super(Event.UNSUBSCRIBED, timestamp);
        this.userId = checkNotEmpty(userId);
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final IncomingUnsubscribeEvent that = (IncomingUnsubscribeEvent) o;

        return userId != null ? userId.equals(that.userId) : that.userId == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}
