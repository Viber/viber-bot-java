package com.viber.bot;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import static com.viber.bot.Preconditions.checkNotEmpty;

public class ViberSignatureValidator {

    private static final Logger logger = LoggerFactory.getLogger(ViberSignatureValidator.class);
    private final String secret;

    public ViberSignatureValidator(final @Nonnull String secret) {
        this.secret = checkNotEmpty(secret);
    }

    private static String encode(final @Nonnull String key, final @Nonnull String data) {
        final byte[] bytes = Hashing.hmacSha256(key.getBytes()).hashString(data, Charsets.UTF_8).asBytes();
        return toHex(bytes);
    }

    private static String toHex(final byte[] bytes) {
        String hex = "";
        for (final byte b : bytes) {
            hex += toHex(b);
        }
        return hex;
    }

    private static String toHex(final byte b) {
        return Integer.toString((b & 0xff) + 0x100, 16).substring(1);
    }

    public boolean isSignatureValid(final @Nonnull String signature, final @Nonnull String data) {
        final String calculatedHash = encode(secret, data);
        logger.debug("Validating signature '{}' == '{}'", signature, calculatedHash);
        return calculatedHash.equals(signature);
    }
}
