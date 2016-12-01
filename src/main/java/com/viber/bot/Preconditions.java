package com.viber.bot;

import com.google.common.base.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class Preconditions {
    public static String checkNotEmpty(final @Nonnull String str) {
        return checkNotEmpty(str, null);
    }

    public static String checkNotEmpty(final @Nonnull String str, final @Nullable String errorMessage) {
        return checkNotNull(Strings.emptyToNull(str), errorMessage);
    }
}
