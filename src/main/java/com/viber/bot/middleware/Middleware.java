package com.viber.bot.middleware;

import com.google.common.util.concurrent.ListenableFuture;
import com.viber.bot.Request;

import javax.annotation.concurrent.ThreadSafe;
import java.io.InputStream;

@ThreadSafe
public interface Middleware {
    ListenableFuture<InputStream> incoming(final Request request);
}
