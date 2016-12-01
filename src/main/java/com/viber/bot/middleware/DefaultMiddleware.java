package com.viber.bot.middleware;

import com.google.common.util.concurrent.ListenableFuture;
import com.viber.bot.Request;

import javax.annotation.Nonnull;
import java.io.InputStream;

public class DefaultMiddleware implements Middleware {

    private final Middleware middleware;

    public DefaultMiddleware(final @Nonnull RequestReceiver requestReceiver) {
        middleware = new PubSubMiddleware(requestReceiver);
    }

    @Override
    public ListenableFuture<InputStream> incoming(final Request request) {
        return middleware.incoming(request);
    }
}
