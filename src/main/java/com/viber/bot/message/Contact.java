package com.viber.bot.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static com.viber.bot.Preconditions.checkNotEmpty;

@Immutable
public class Contact {

	private final String name;
	private final String phoneNumber;

	@Nullable
	private final String avatar;

	@JsonCreator
	public Contact(final @JsonProperty("name") @Nonnull String contactName,
			final @JsonProperty("phone_number") @Nonnull String contactPhoneNumber,
			final @JsonProperty("avatar") @Nullable String avatar) {
		this.name = checkNotEmpty(contactName);
		this.phoneNumber = checkNotEmpty(contactPhoneNumber);
		this.avatar = Strings.emptyToNull(avatar);
	}

	public String getName() {
		return name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	@Nullable
	public String getAvatar() {
		return avatar;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Contact contact = (Contact) o;

		if (name != null ? !name.equals(contact.name) : contact.name != null) return false;
		if (phoneNumber != null ? !phoneNumber.equals(contact.phoneNumber) : contact.phoneNumber != null) return false;
		return avatar != null ? avatar.equals(contact.avatar) : contact.avatar == null;
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
		result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
		return result;
	}
}
