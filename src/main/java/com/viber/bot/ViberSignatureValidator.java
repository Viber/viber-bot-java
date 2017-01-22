package com.viber.bot;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
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

    public boolean isSignatureValid(final @Nonnull String signature, final @Nonnull String data) {
        final String calculatedHash = encode(secret, data);
        logger.debug("Validating signature '{}' == '{}'", signature, calculatedHash);
        return calculatedHash.equals(signature);
    }

    private String encode(final @Nonnull String key, final @Nonnull String data) {
        final byte[] bytes = Hashing.hmacSha256(key.getBytes()).hashString(data, Charsets.UTF_8).asBytes();
        return BaseEncoding.base16().lowerCase().encode(bytes);
    }
}
