package com.viber.bot.profile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static com.viber.bot.Preconditions.checkNotEmpty;

@Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {

    private final String id;
    private final String name;

    @Nullable
    private final String avatar;

    @Nullable
    private final String country;

    @Nullable
    private final String language;

    @JsonCreator
    UserProfile(final @JsonProperty("id") @Nonnull String id,
                final @JsonProperty("name") @Nonnull String name,
                final @JsonProperty("avatar") @Nullable String avatar,
                final @JsonProperty("country") @Nullable String country,
                final @JsonProperty("language") @Nullable String language) {

        this.id = checkNotEmpty(id);
        this.name = checkNotEmpty(name);
        this.avatar = Strings.emptyToNull(avatar);
        this.country = Strings.emptyToNull(country);
        this.language = Strings.emptyToNull(language);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getAvatar() {
        return avatar;
    }

    @Nullable
    public String getCountry() {
        return country;
    }

    @Nullable
    public String getLanguage() {
        return language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserProfile that = (UserProfile) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (avatar != null ? !avatar.equals(that.avatar) : that.avatar != null) return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        return language != null ? language.equals(that.language) : that.language == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        return result;
    }
}
