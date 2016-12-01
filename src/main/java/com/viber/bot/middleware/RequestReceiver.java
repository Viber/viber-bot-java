package com.viber.bot.middleware;

import com.viber.bot.Request;

public interface RequestReceiver {
    void acceptRequest(final Request request);
}
