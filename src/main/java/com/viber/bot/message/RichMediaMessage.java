package com.viber.bot.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Immutable
public class RichMediaMessage extends Message {

    private final static int RICH_MEDIA_MINIMUM_API_VERSION = 2;

    @Nonnull
    private final RichMediaObject richMediaObject;

    @Nullable
    private final String alternativeText;

    @Nullable
    private final Integer minimalApiVersion;

    @JsonCreator
    public RichMediaMessage(final @JsonProperty("rich_media") @Nonnull RichMediaObject richMediaObject,
                            final @JsonProperty("alt_text") @Nullable String alternativeText,
                            final @JsonProperty("min_api_version") @Nullable Integer minimalApiVersion,
                            final @JsonProperty("keyboard") @Nullable MessageKeyboard keyboard,
                            final @JsonProperty("tracking_data") @Nullable TrackingData trackingData) {
        super("rich_media", keyboard, trackingData);
        this.richMediaObject = checkNotNull(richMediaObject);
        this.alternativeText = alternativeText;
        this.minimalApiVersion = minimalApiVersion;
    }

    @JsonIgnore
    public RichMediaMessage(final RichMediaObject richMedia, final String alternativeText, final Integer minimalApiVersion) {
        this(richMedia, alternativeText, minimalApiVersion, null, null);
    }

    @Override
    protected Map<String, Object> getPartialMapRepresentation() {
        return new HashMap<String, Object>() {{
            put("rich_media", getRichMediaObject());
            put("alt_text", getAlternativeText());
            put("min_api_version", getMinimalApiVersion().orElse(RICH_MEDIA_MINIMUM_API_VERSION));
        }};
    }

    @Nonnull
    public RichMediaObject getRichMediaObject() {
        return richMediaObject;
    }

    @Nullable
    public String getAlternativeText() {
        return alternativeText;
    }

    @Nullable
    public Optional<Integer> getMinimalApiVersion() {
        return Optional.ofNullable(minimalApiVersion);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final RichMediaMessage that = (RichMediaMessage) o;

        if (!richMediaObject.equals(that.richMediaObject)) return false;
        if (minimalApiVersion != null ? !minimalApiVersion.equals(that.minimalApiVersion) : that.minimalApiVersion != null)
            return false;
        return alternativeText != null ? !alternativeText.equals(that.alternativeText) : that.alternativeText != null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + richMediaObject.hashCode();
        result = 31 * result + (minimalApiVersion != null ? minimalApiVersion.hashCode() : 0);
        result = 31 * result + (alternativeText != null ? alternativeText.hashCode() : 0);
        return result;
    }
}
