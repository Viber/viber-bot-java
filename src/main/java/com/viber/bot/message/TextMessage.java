package com.viber.bot.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;

import static com.viber.bot.Preconditions.checkNotEmpty;

@Immutable
public class TextMessage extends Message {

    private final String text;

    @JsonCreator
    public TextMessage(final @JsonProperty("text") @Nonnull String text,
                       final @JsonProperty("keyboard") @Nullable MessageKeyboard keyboard,
                       final @JsonProperty("tracking_data") @Nullable TrackingData trackingData) {
        super("text", keyboard, trackingData);
        this.text = checkNotEmpty(text);
    }

    @JsonIgnore
    public TextMessage(final @Nonnull String text) {
        this(text, null, null);
    }

    @Override
    protected Map<String, Object> getPartialMapRepresentation() {
        return new HashMap<String, Object>() {{
            put("text", getText());
        }};
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final TextMessage that = (TextMessage) o;
        return text != null ? text.equals(that.text) : that.text == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}
