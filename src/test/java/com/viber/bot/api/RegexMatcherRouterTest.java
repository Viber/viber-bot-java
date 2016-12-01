package com.viber.bot.api;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class RegexMatcherRouterTest {

    private final RegexMatcherRouter regexMatcherRouter = new RegexMatcherRouter();

    @Test
    public void testRegexMatcherRouterSanity() {
        final AtomicBoolean isMessageReceived = new AtomicBoolean(false);

        regexMatcherRouter.newMatcher(Pattern.compile("hi", Pattern.CASE_INSENSITIVE),
                (event, message, response) -> isMessageReceived.compareAndSet(false, true));
        regexMatcherRouter.tryGetCallback("Hi").forEach(callback -> callback.emit(null, null, null));

        assertThat(isMessageReceived.get()).isEqualTo(true);
    }

    @Test
    public void testRegexMatcherRouterMultipleMatches() {
        final AtomicInteger numberOfMatches = new AtomicInteger(0);

        regexMatcherRouter.newMatcher(Pattern.compile("hi", Pattern.CASE_INSENSITIVE),
                (event, message, response) -> numberOfMatches.incrementAndGet());

        regexMatcherRouter.newMatcher(Pattern.compile("(hi|hello)", Pattern.CASE_INSENSITIVE),
                (event, message, response) -> numberOfMatches.incrementAndGet());

        regexMatcherRouter.tryGetCallback("Hi").forEach(callback -> callback.emit(null, null, null));
        assertThat(numberOfMatches.get()).isEqualTo(2);
    }

    @Test
    public void testRegexMatcherRouterNoMatches() {
        final AtomicBoolean isMessageReceived = new AtomicBoolean(false);

        regexMatcherRouter.newMatcher(Pattern.compile("hi", Pattern.CASE_INSENSITIVE),
                (event, message, response) -> isMessageReceived.compareAndSet(false, true));
        regexMatcherRouter.tryGetCallback("Hello").forEach(callback -> callback.emit(null, null, null));

        assertThat(isMessageReceived.get()).isEqualTo(false);
    }
}