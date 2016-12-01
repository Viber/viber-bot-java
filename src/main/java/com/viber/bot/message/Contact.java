package com.viber.bot.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.viber.bot.Preconditions.checkNotEmpty;

@Immutable
public class Contact {

    private final String name;
    private final String phoneNumber;

    @JsonCreator
    public Contact(final @JsonProperty("name") @Nonnull String contactName,
                   final @JsonProperty("phone_number") @Nonnull String contactPhoneNumber) {
        this.name = checkNotEmpty(contactName);
        this.phoneNumber = checkNotEmpty(contactPhoneNumber);
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Contact contact = (Contact) o;

        if (name != null ? !name.equals(contact.name) : contact.name != null) return false;
        return phoneNumber != null ? phoneNumber.equals(contact.phoneNumber) : contact.phoneNumber == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        return result;
    }
}
