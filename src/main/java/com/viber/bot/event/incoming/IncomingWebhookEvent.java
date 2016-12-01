package com.viber.bot.event.incoming;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viber.bot.event.Event;

import javax.annotation.concurrent.Immutable;

@Immutable
public class IncomingWebhookEvent extends IncomingEvent {

    private final Long token;

    @JsonCreator
    IncomingWebhookEvent(final @JsonProperty("message_token") long token,
                         final @JsonProperty("timestamp") long timestamp) {
        super(Event.WEBHOOK, timestamp);
        this.token = token;
    }

    public Long getToken() {
        return token;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final IncomingWebhookEvent that = (IncomingWebhookEvent) o;

        return token != null ? token.equals(that.token) : that.token == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }
}
