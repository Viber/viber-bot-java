package com.viber.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.viber.bot.event.incoming.IncomingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.ThreadSafe;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;

@ThreadSafe
public class Request implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(Request.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String EMPTY_JSON_OBJECT = "{}";

    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final AtomicReference<InputStream> responseInputStream = new AtomicReference<>();
    private final IncomingEvent event;

    private Request(final String json) {
        logger.debug("Reading json request: {}", json);
        event = readJson(json, IncomingEvent.class);
    }

    /**
     * Creates a Request object from an InputStream.
     * Be noted that the InputStream will be closed by this method.
     *
     * @param inputStream the InputStream
     * @return Request
     */
    public static Request fromInputStream(final @WillClose @Nonnull InputStream inputStream) {
        final String json = new String(readInputStream(inputStream), Charsets.UTF_8);
        Closeables.closeQuietly(inputStream);
        return Request.fromJsonString(json);
    }

    /**
     * Creates a Request object from a json string literal.
     *
     * @param json string
     * @return Request
     */
    public static Request fromJsonString(final @Nullable String json) {
        return new Request(MoreObjects.firstNonNull(Strings.nullToEmpty(json), EMPTY_JSON_OBJECT));
    }

    private static byte[] readInputStream(final @Nonnull InputStream inputStream) {
        final byte[] data;
        try {
            data = ByteStreams.toByteArray(inputStream);
        } catch (final IOException e) {
            logger.error("Could not read input stream", e);
            throw new RuntimeException(e);
        }
        return data;
    }

    private static <T> T readJson(final String json, final Class<T> clazz) {
        final T event;
        try {
            event = objectMapper.readValue(json, clazz);
        } catch (final Exception e) {
            logger.error("Could not read incoming event", e);
            throw new RuntimeException(e);
        }
        return event;
    }

    public IncomingEvent getEvent() {
        return event;
    }

    public boolean isClosed() {
        return isClosed.get();
    }

    public void setResponse(final String response) {
        setResponse(new ByteArrayInputStream(response.getBytes(Charsets.UTF_16)));
    }

    public void setResponse(final @Nonnull @WillNotClose InputStream inputStream) {
        responseInputStream.set(checkNotNull(inputStream));
    }

    @Nullable
    public InputStream getResponseInputStream() {
        return responseInputStream.get();
    }

    @Override
    public void close() throws IOException {
        synchronized (this) {
            if (!isClosed.compareAndSet(false, true)) return;
            try {
                notify();
            } catch (final IllegalMonitorStateException e) {
                logger.error("Could not notify object", e);
            }
        }
    }
}
