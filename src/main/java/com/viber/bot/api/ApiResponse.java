package com.viber.bot.api;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ForwardingMap;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

/**
 * Simple Map(String, Object) wrapper class.
 */
public class ApiResponse extends ForwardingMap<String, Object> {
    private final Map<String, Object> map;

    ApiResponse(final @Nullable Map<String, Object> delegate) {
        this.map = Collections.unmodifiableMap(MoreObjects.firstNonNull(delegate, Collections.emptyMap()));
    }

    @Override
    protected Map<String, Object> delegate() {
        return map;
    }
}
