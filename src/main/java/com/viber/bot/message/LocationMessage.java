package com.viber.bot.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Immutable
public class LocationMessage extends Message {

    private final Location location;

    @JsonCreator
    public LocationMessage(final @JsonProperty("location") @Nonnull Location location,
                           final @JsonProperty("keyboard") @Nullable MessageKeyboard keyboard,
                           final @JsonProperty("tracking_data") @Nullable TrackingData trackingData) {
        super("location", keyboard, trackingData);
        this.location = checkNotNull(location);
    }

    @JsonIgnore
    public LocationMessage(final @Nonnull Location location) {
        this(location, null, null);
    }

    @Override
    protected Map<String, Object> getPartialMapRepresentation() {
        return new HashMap<String, Object>() {{
            put("location", new HashMap<String, Object>() {{
                put("lat", getLocation().getLatitude());
                put("lon", getLocation().getLongitude());
            }});
        }};
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final LocationMessage that = (LocationMessage) o;

        return location != null ? location.equals(that.location) : that.location == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }
}
