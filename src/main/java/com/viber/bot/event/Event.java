package com.viber.bot.event;

import javax.annotation.Nullable;

public enum Event {

    MESSAGE_SENT,
    MESSAGE_RECEIVED("message"),
    MESSAGE_DELIVERED("delivered"),
    MESSAGE_SEEN("seen"),

    SUBSCRIBED("subscribed"),
    UNSUBSCRIBED("unsubscribed"),

    CONVERSATION_STARTED("conversation_opened"),
    WEBHOOK("webhook"),

    ERROR("failed");

    private final String serverEventName;

    Event(final @Nullable String serverEventName) {
        this.serverEventName = serverEventName;
    }

    Event() {
        this(null);
    }

    @Nullable
    public String getServerEventName() {
        return serverEventName;
    }
}
