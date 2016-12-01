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
public class FileMessage extends Message {

    private final String url;
    private final int sizeInBytes;
    private final String filename;

    @JsonCreator
    public FileMessage(final @JsonProperty("media") @Nonnull String url,
                       final @JsonProperty("size") int sizeInBytes,
                       final @JsonProperty("file_name") @Nonnull String filename,
                       final @JsonProperty("keyboard") @Nullable MessageKeyboard keyboard,
                       final @JsonProperty("tracking_data") @Nullable TrackingData trackingData) {
        super("file", keyboard, trackingData);
        this.url = checkNotEmpty(url);
        this.sizeInBytes = sizeInBytes;
        this.filename = checkNotEmpty(filename);
    }

    @JsonIgnore
    public FileMessage(final @Nonnull String url, final int sizeInBytes, final @Nonnull String filename) {
        this(url, sizeInBytes, filename, null, null);
    }

    @Override
    protected Map<String, Object> getPartialMapRepresentation() {
        return new HashMap<String, Object>() {{
            put("media", getUrl());
            put("size", getSizeInBytes());
            put("file_name", getFilename());
        }};
    }

    public String getUrl() {
        return url;
    }

    public int getSizeInBytes() {
        return sizeInBytes;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final FileMessage that = (FileMessage) o;

        if (sizeInBytes != that.sizeInBytes) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        return filename != null ? filename.equals(that.filename) : that.filename == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + sizeInBytes;
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        return result;
    }
}
