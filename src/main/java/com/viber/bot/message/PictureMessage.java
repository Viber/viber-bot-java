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

import static com.viber.bot.Preconditions.checkNotEmpty;

@Immutable
public class PictureMessage extends Message {

    private final String url;

    @Nullable
    private final String text;

    @Nullable
    private final String thumbnail;

    @JsonCreator
    public PictureMessage(final @JsonProperty("media") @Nonnull String url,
                          final @JsonProperty("text") @Nullable String text,
                          final @JsonProperty("thumbnail") @Nullable String thumbnail,
                          final @JsonProperty("keyboard") @Nullable MessageKeyboard keyboard,
                          final @JsonProperty("tracking_data") @Nullable TrackingData trackingData) {
        super("picture", keyboard, trackingData);
        this.url = checkNotEmpty(url);
        this.text = Strings.emptyToNull(text);
        this.thumbnail = Strings.emptyToNull(thumbnail);
    }

    @JsonIgnore
    public PictureMessage(final @Nonnull String url, final @Nullable String text, final @Nullable String thumbnail) {
        this(url, text, thumbnail, null, null);
    }

    @Override
    protected Map<String, Object> getPartialMapRepresentation() {
        return new HashMap<String, Object>() {{
            put("media", getUrl());
            put("text", getText());
            put("thumbnail", getThumbnail());
        }};
    }

    public String getUrl() {
        return url;
    }

    @Nullable
    public String getText() {
        return text;
    }

    @Nullable
    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final PictureMessage that = (PictureMessage) o;

        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        return thumbnail != null ? thumbnail.equals(that.thumbnail) : that.thumbnail == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (thumbnail != null ? thumbnail.hashCode() : 0);
        return result;
    }
}
