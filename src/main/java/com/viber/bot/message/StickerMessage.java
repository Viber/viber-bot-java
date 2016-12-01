package com.viber.bot.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;

@Immutable
public class StickerMessage extends Message {

    private final long stickerId;

    @JsonCreator
    public StickerMessage(final @JsonProperty("sticker_id") long stickerId,
                          final @JsonProperty("keyboard") @Nullable MessageKeyboard keyboard,
                          final @JsonProperty("tracking_data") @Nullable TrackingData trackingData) {
        super("sticker", keyboard, trackingData);
        this.stickerId = stickerId;
    }

    @JsonIgnore
    public StickerMessage(final long stickerId) {
        this(stickerId, null, null);
    }

    @Override
    protected Map<String, Object> getPartialMapRepresentation() {
        return new HashMap<String, Object>() {{
            put("sticker_id", getStickerId());
        }};
    }

    public long getStickerId() {
        return stickerId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final StickerMessage that = (StickerMessage) o;

        return stickerId == that.stickerId;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (stickerId ^ (stickerId >>> 32));
        return result;
    }
}
