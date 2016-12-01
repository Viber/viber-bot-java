package com.viber.bot.middleware;

import com.viber.bot.Request;
import org.assertj.core.util.Closeables;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class PubSubMiddlewareTest {

    @Mock
    private Request request;

    @Test
    public void testMiddlewareSanity() {
        final AtomicBoolean gotRequest = new AtomicBoolean(false);
        final Middleware middleware = new PubSubMiddleware(request -> {
            gotRequest.compareAndSet(false, true);
            Closeables.closeQuietly(request);
        });

        middleware.incoming(request);
        await().untilAtomic(gotRequest, equalTo(true));
    }

    @Test
    public void testMiddlewareMultipleRequestsTestSequence() {
        final int nRequests = 10;
        final AtomicInteger numberOfRequests = new AtomicInteger(0);
        final Middleware middleware = new PubSubMiddleware(request -> {
            numberOfRequests.incrementAndGet();
            Closeables.closeQuietly(request);
        });

        IntStream.range(0, nRequests).forEach(i -> middleware.incoming(request));
        await().untilAtomic(numberOfRequests, equalTo(nRequests));
    }

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);

        final AtomicBoolean isRequestClosed = new AtomicBoolean(false);
        doAnswer(invocation -> {
            synchronized (request) {
                request.notify();
                isRequestClosed.set(true);
            }
            return null;
        }).when(request).close();
        when(request.isClosed()).then(invocation -> isRequestClosed.get());
    }

    @Test(expected = NullPointerException.class)
    public void testMiddlewareNullRequest() {
        final Middleware middleware = new PubSubMiddleware(request -> {
        });
        middleware.incoming(null);
    }
}