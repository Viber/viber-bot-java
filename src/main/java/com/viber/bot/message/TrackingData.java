package com.viber.bot.message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ForwardingMap;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Simple Map(String, Object) wrapper class.
 */
@Immutable
@JsonDeserialize(using = TrackingData.TrackingDataDeserializer.class)
public class TrackingData extends ForwardingMap<String, Object> {
    private final Map<String, Object> map;

    public TrackingData(final @Nullable Map<String, Object> delegate) {
        this.map = Collections.unmodifiableMap(MoreObjects.firstNonNull(delegate, Collections.emptyMap()));
    }

    public TrackingData() {
        this(null);
    }

    @Override
    protected Map<String, Object> delegate() {
        return map;
    }

    static class TrackingDataDeserializer extends JsonDeserializer<TrackingData> {
        private static final ObjectMapper objectMapper = new ObjectMapper();
        private static final String EMPTY_JSON_OBJECT = "{}";

        @Override
        public TrackingData deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return new TrackingData(objectMapper.readValue(MoreObjects.firstNonNull(Strings.emptyToNull(p.getValueAsString().trim()), EMPTY_JSON_OBJECT), Map.class));
        }
    }
}
