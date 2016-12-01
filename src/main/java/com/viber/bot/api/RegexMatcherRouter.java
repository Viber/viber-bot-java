package com.viber.bot.api;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.viber.bot.event.callback.OnMessageReceived;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

class RegexMatcherRouter {
    private final Multimap<Pattern, OnMessageReceived> patterns = ArrayListMultimap.create();

    void newMatcher(final @Nonnull Pattern pattern, final @Nonnull OnMessageReceived onMessageReceived) {
        patterns.put(checkNotNull(pattern), checkNotNull(onMessageReceived));
    }

    List<OnMessageReceived> tryGetCallback(final String text) {
        return patterns.asMap().entrySet().stream()
                .filter(entry -> entry.getKey().matcher(text).find())
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());
    }
}
