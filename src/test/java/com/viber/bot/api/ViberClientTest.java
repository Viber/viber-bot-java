package com.viber.bot.api;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ViberClientTest {

    private static final ViberClient client = new ViberClient("http://url.com", "TOKEN");
    private static final RequestBody EMPTY_REQUEST_BODY = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

    @Test
    public void testCreateRequestAuthTokenExistsSanity() {
        Request request = client.createRequest(ViberClient.Endpoint.SEND_MESSAGE, EMPTY_REQUEST_BODY);
        assertThat(request.headers().get(ViberClient.VIBER_AUTH_TOKEN_HEADER)).isNotEmpty();
        assertThat(request.headers().get(ViberClient.VIBER_AUTH_TOKEN_HEADER)).isEqualTo("TOKEN");
    }

    @Test
    public void testCreateRequestUserAgentExistsSanity() {
        Request request = client.createRequest(ViberClient.Endpoint.SEND_MESSAGE, EMPTY_REQUEST_BODY);
        assertThat(request.headers().get(ViberClient.USER_AGENT_HEADER_FIELD)).isNotEmpty();
        assertThat(request.headers().get(ViberClient.USER_AGENT_HEADER_FIELD))
                .isEqualTo(String.format("%s%s", ViberClient.USER_AGENT_HEADER_VALUE, ViberClient.VIBER_LIBRARY_VERSION));
    }
}