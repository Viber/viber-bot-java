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
    private final String thumbnail;

    @Nullable
    private final Integer duration;

    @JsonCreator
    public VideoMessage(final @JsonProperty("media") @Nonnull String url,
                        final @JsonProperty("size") int size,
                        final @JsonProperty("thumbnail") @Nullable String thumbnail,
                        final @JsonProperty("duration") @Nullable Integer duration,
                        final @JsonProperty("keyboard") @Nullable MessageKeyboard keyboard,
                        final @JsonProperty("tracking_data") @Nullable TrackingData trackingData) {
        super("video", keyboard, trackingData);
        this.url = checkNotEmpty(url);
        this.size = size;
        this.thumbnail = Strings.emptyToNull(thumbnail);
        this.duration = duration;
    }

    @JsonIgnore
    public VideoMessage(final @Nonnull String url, final int size,
                        final @Nullable String thumbnail, final @Nullable Integer duration) {
        this(url, size, thumbnail, duration, null, null);
    }

    @Override
    protected Map<String, Object> getPartialMapRepresentation() {
        return new HashMap<String, Object>() {{
            put("media", getUrl());
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
    public String getThumbnail() {
        return thumbnail;
    }

    public Optional<Integer> getDuration() {
        return Optional.ofNullable(duration);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final VideoMessage that = (VideoMessage) o;

        if (size != that.size) return false;
        if (duration != that.duration) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        return thumbnail != null ? thumbnail.equals(that.thumbnail) : that.thumbnail == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (thumbnail != null ? thumbnail.hashCode() : 0);
        result = 31 * result + size;
        result = 31 * result + duration;
        return result;
    }
}
