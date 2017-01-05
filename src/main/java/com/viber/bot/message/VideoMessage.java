package com.viber.bot.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.viber.bot.Preconditions.checkNotEmpty;

@Immutable
public class VideoMessage extends Message {

    private final String url;
    private final int size;

    @Nullable
    private final String text;

    @Nullable
    private final String thumbnail;

    @Nullable
    private final Integer duration;

    @JsonCreator
    public VideoMessage(final @JsonProperty("media") @Nonnull String url,
                        final @JsonProperty("size") int size,
                        final @JsonProperty("text") @Nullable String text,
                        final @JsonProperty("thumbnail") @Nullable String thumbnail,
                        final @JsonProperty("duration") @Nullable Integer duration,
                        final @JsonProperty("keyboard") @Nullable MessageKeyboard keyboard,
                        final @JsonProperty("tracking_data") @Nullable TrackingData trackingData) {
        super("video", keyboard, trackingData);
        this.url = checkNotEmpty(url);
        this.size = size;
        this.text = text;
        this.thumbnail = Strings.emptyToNull(thumbnail);
        this.duration = duration;
    }

    @JsonIgnore
    public VideoMessage(final @Nonnull String url, final int size, final @Nullable String text,
                        final @Nullable String thumbnail, final @Nullable Integer duration) {
        this(url, size, text, thumbnail, duration, null, null);
    }

    @Override
    protected Map<String, Object> getPartialMapRepresentation() {
        return new HashMap<String, Object>() {{
            put("media", getUrl());
            put("text", getText());
            put("thumbnail", getThumbnail());
            put("size", getSize());
            put("duration", getDuration());
        }};
    }

    public String getUrl() {
        return url;
    }

    public int getSize() {
        return size;
    }

    @Nullable
    public String getText() {
        return text;
    }

    @Nullable
    public String getThumbnail() {
        return thumbnail;
    }

    public Optional<Integer> getDuration() {
        return Optional.ofNullable(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        VideoMessage that = (VideoMessage) o;

        if (size != that.size) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (thumbnail != null ? !thumbnail.equals(that.thumbnail) : that.thumbnail != null) return false;
        return duration != null ? duration.equals(that.duration) : that.duration == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + size;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (thumbnail != null ? thumbnail.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        return result;
    }
}
