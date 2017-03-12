package com.viber.bot.message;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;

import static com.viber.bot.Preconditions.checkNotEmpty;

@Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = RichMediaMessage.class, name = "rich_media"),
        @JsonSubTypes.Type(value = TextMessage.class, name = "text"),
        @JsonSubTypes.Type(value = ContactMessage.class, name = "contact"),
        @JsonSubTypes.Type(value = FileMessage.class, name = "file"),
        @JsonSubTypes.Type(value = LocationMessage.class, name = "location"),
        @JsonSubTypes.Type(value = PictureMessage.class, name = "picture"),
        @JsonSubTypes.Type(value = StickerMessage.class, name = "sticker"),
        @JsonSubTypes.Type(value = UrlMessage.class, name = "url"),
        @JsonSubTypes.Type(value = VideoMessage.class, name = "video")
})
public abstract class Message { // todo: should be case classes when moving to scala

    private final String type;
    private final MessageKeyboard keyboard;
    private final TrackingData trackingData;

    Message(final @Nonnull String type,
            final @Nullable MessageKeyboard keyboard,
            final @Nullable TrackingData trackingData) {

        this.type = checkNotEmpty(type);
        this.keyboard = MoreObjects.firstNonNull(keyboard, new MessageKeyboard());
        this.trackingData = MoreObjects.firstNonNull(trackingData, new TrackingData());
    }

    public String getType() {
        return type;
    }

    public MessageKeyboard getKeyboard() {
        return keyboard;
    }

    public TrackingData getTrackingData() {
        return trackingData;
    }
    
    @JsonAnyGetter
    public Map<String, Object> getMapRepresentation() {
        return new HashMap<String, Object>() {{
            put("type", getType());
            putAll(getPartialMapRepresentation());
        }};
    }

    protected abstract Map<String, Object> getPartialMapRepresentation();

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Message message = (Message) o;

        if (type != null ? !type.equals(message.type) : message.type != null) return false;
        if (keyboard != null ? !keyboard.equals(message.keyboard) : message.keyboard != null) return false;
        return trackingData != null ? trackingData.equals(message.trackingData) : message.trackingData == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (keyboard != null ? keyboard.hashCode() : 0);
        result = 31 * result + (trackingData != null ? trackingData.hashCode() : 0);
        return result;
    }
}
