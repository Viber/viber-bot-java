package com.viber.bot.message;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ForwardingMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Simple Map(String, Object) wrapper class.
 */
@Immutable
@JsonDeserialize(using = RichMediaObject.RichMediaDeserializer.class)
public class RichMediaObject extends ForwardingMap<String, Object> {
    private final Map<String, Object> map;

    public RichMediaObject(final @Nullable Map<String, Object> delegate) {
        this.map = Collections.unmodifiableMap(MoreObjects.firstNonNull(delegate, Collections.emptyMap()));
    }

    public RichMediaObject() {
        this(null);
    }

    @Override
    protected Map<String, Object> delegate() {
        return map;
    }

    static class RichMediaDeserializer extends JsonDeserializer<RichMediaObject> {
        private static final Logger logger = LoggerFactory.getLogger(RichMediaDeserializer.class);
        private static final ObjectMapper objectMapper = new ObjectMapper();
        private static final String EMPTY_JSON_OBJECT = "{}";

        @Override
        public RichMediaObject deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            Map<String, Object> richMediaMap = null;
            try {
                richMediaMap = objectMapper.readValue(MoreObjects.firstNonNull(
                        Strings.emptyToNull(p.getValueAsString().trim()), EMPTY_JSON_OBJECT), Map.class);
            } catch (JsonMappingException | JsonParseException exception) {
                logger.warn("Could not deserialize message keyboard '{}'", p.getValueAsString().trim());
            }
            return new RichMediaObject(richMediaMap);
        }
    }
}
