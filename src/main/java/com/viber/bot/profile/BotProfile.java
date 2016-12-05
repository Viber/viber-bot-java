package com.viber.bot.profile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static com.viber.bot.Preconditions.checkNotEmpty;

@Immutable
public class BotProfile {

    private final String name;
    private final String avatar;

    public BotProfile(final @Nonnull String name, final @Nullable String avatar) {
        this.name = checkNotEmpty(name);
        this.avatar = avatar;
    }

    public BotProfile(final @Nonnull String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getAvatar() {
        return avatar;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final BotProfile that = (BotProfile) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return avatar != null ? avatar.equals(that.avatar) : that.avatar == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (avatar != null ? avatar.hashCode() : 0);
        return result;
    }
}
