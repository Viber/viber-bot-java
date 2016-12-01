package com.viber.bot.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.UrlEscapers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;

import static com.viber.bot.Preconditions.checkNotEmpty;

@Immutable
public class UrlMessage extends Message {

    private final String url;

    @JsonCreator
    public UrlMessage(final @JsonProperty("media") @Nonnull String url,
                      final @JsonProperty("keyboard") @Nullable MessageKeyboard keyboard,
                      final @JsonProperty("tracking_data") @Nullable TrackingData trackingData) {
        super("url", keyboard, trackingData);
        this.url = UrlEscapers.urlPathSegmentEscaper().escape(checkNotEmpty(url));
    }

    @JsonIgnore
    public UrlMessage(final @Nonnull String url) {
        this(url, null, null);
    }

    @Override
    protected Map<String, Object> getPartialMapRepresentation() {
        return new HashMap<String, Object>() {{
            put("media", getUrl());
        }};
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final UrlMessage that = (UrlMessage) o;

        return url != null ? url.equals(that.url) : that.url == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}
