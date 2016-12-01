package com.viber.bot;

import com.viber.bot.event.incoming.IncomingDeliveredEvent;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestTest {

    private static final String VALID_JSON = "{\"event\":\"delivered\",\"timestamp\":1457764197627,\"message_token\":4912661846655238145,\"user_id\":\"01234567890A=\"}";

    @Test
    public void testValidJson() {
        final Request request = Request.fromJsonString(VALID_JSON);
        assertThat(request.getEvent()).isInstanceOf(IncomingDeliveredEvent.class);
    }

    @Test
    public void testValidInputStream() {
        final Request request = Request.fromInputStream(new ByteArrayInputStream(VALID_JSON.getBytes(Charset.defaultCharset())));
        assertThat(request.getEvent()).isInstanceOf(IncomingDeliveredEvent.class);
    }

    @Test(expected = RuntimeException.class)
    public void testJsonWithMissingFields() {
        Request.fromJsonString("{\"event\":\"delivered\"}");
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidJson() {
        Request.fromJsonString("{");
    }

    @Test(expected = RuntimeException.class)
    public void testJsonWithInvalidTypeInField() {
        Request.fromJsonString("{\"event\":\"delivered\",\"timestamp\":\"hi\"}");
    }
}