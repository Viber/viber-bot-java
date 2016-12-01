package com.viber.bot;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ViberSignatureValidatorTest {

    private static final String AUTH_TOKEN = "44dafb7e0f40021e-61a47a1e6778d187-f2c5a676a07050b3";
    private static final String VALID_SIGNATURE = "d21b343448c8aee33b8e93768ef6ceb64a6ba6163099973a2b8bd028fea510ef";
    private static final String SERVER_MESSAGE = "{\"event\":\"webhook\",\"timestamp\":4977069964384421269,\"message_token\":1478683725125}";

    @Test
    public void testSignatureValidationSanity() {
        final ViberSignatureValidator validator = new ViberSignatureValidator(AUTH_TOKEN);
        assertThat(validator.isSignatureValid(VALID_SIGNATURE, SERVER_MESSAGE)).isTrue();
    }

    @Test
    public void testSignatureIsInvalid() {
        final ViberSignatureValidator validator = new ViberSignatureValidator("abc");
        assertThat(validator.isSignatureValid(VALID_SIGNATURE, SERVER_MESSAGE)).isFalse();
    }

    @Test
    public void testInvalidDataInSignature() {
        final ViberSignatureValidator validator = new ViberSignatureValidator(AUTH_TOKEN);
        assertThat(validator.isSignatureValid(VALID_SIGNATURE, "{}")).isFalse();
    }

    @Test(expected = NullPointerException.class)
    public void testEmptySecret() {
        new ViberSignatureValidator(null);
    }

}