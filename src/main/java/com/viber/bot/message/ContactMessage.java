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
public class ContactMessage extends Message {

    private final Contact contact;

    @JsonCreator
    public ContactMessage(final @JsonProperty("contact") @Nonnull Contact contact,
                          final @JsonProperty("keyboard") @Nullable MessageKeyboard keyboard,
                          final @JsonProperty("tracking_data") @Nullable TrackingData trackingData) {
        super("contact", keyboard, trackingData);
        this.contact = checkNotNull(contact);
    }

    @JsonIgnore
    public ContactMessage(final @Nonnull Contact contact) {
        this(contact, null, null);
    }

    @Override
    protected Map<String, Object> getPartialMapRepresentation() {
        return new HashMap<String, Object>() {{
            put("contact", new HashMap<String, Object>() {{
                put("name", getContact().getName());
                put("phone_number", getContact().getPhoneNumber());
            }});
        }};
    }

    public Contact getContact() {
        return contact;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final ContactMessage that = (ContactMessage) o;

        return contact != null ? contact.equals(that.contact) : that.contact == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (contact != null ? contact.hashCode() : 0);
        return result;
    }
}
