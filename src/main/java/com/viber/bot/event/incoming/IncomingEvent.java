package com.viber.bot.event.incoming;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.viber.bot.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;

@Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "event")
@JsonSubTypes({
        @JsonSubTypes.Type(value = IncomingMessageEvent.class, name = "message"),
        @JsonSubTypes.Type(value = IncomingWebhookEvent.class, name = "webhook"),
        @JsonSubTypes.Type(value = IncomingSeenEvent.class, name = "seen"),
        @JsonSubTypes.Type(value = IncomingDeliveredEvent.class, name = "delivered"),
        @JsonSubTypes.Type(value = IncomingSubscribedEvent.class, name = "subscribed"),
        @JsonSubTypes.Type(value = IncomingUnsubscribeEvent.class, name = "unsubscribed"),
        @JsonSubTypes.Type(value = IncomingConversationStartedEvent.class, name = "conversation_started"),
        @JsonSubTypes.Type(value = IncomingErrorEvent.class, name = "failed")
})
public class IncomingEvent {

    private final Event event;
    private final long timestamp;

    IncomingEvent(final @Nonnull Event event, final long timestamp) {
        this.event = checkNotNull(event);
        this.timestamp = timestamp;
    }

    public Event getEvent() {
        return event;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final IncomingEvent that = (IncomingEvent) o;

        if (timestamp != that.timestamp) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (timestamp ^ (timestamp >>> 32));
    }
}
