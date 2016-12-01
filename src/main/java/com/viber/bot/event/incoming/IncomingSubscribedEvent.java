package com.viber.bot.event.incoming;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viber.bot.event.Event;
import com.viber.bot.profile.UserProfile;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class IncomingSubscribedEvent extends IncomingEvent {

    private final UserProfile user;

    @JsonCreator
    IncomingSubscribedEvent(final @JsonProperty("user") @Nonnull UserProfile user,
                            final @JsonProperty("timestamp") long timestamp) {
        super(Event.SUBSCRIBED, timestamp);
        this.user = user;
    }

    public UserProfile getUser() {
        return user;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final IncomingSubscribedEvent that = (IncomingSubscribedEvent) o;

        return user != null ? user.equals(that.user) : that.user == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}
