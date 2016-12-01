package com.viber.bot.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import com.viber.bot.message.Message;
import com.viber.bot.message.MessageKeyboard;
import com.viber.bot.message.TrackingData;
import com.viber.bot.profile.BotProfile;
import com.viber.bot.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class MessageToMapConverter {

    private static final Logger logger = LoggerFactory.getLogger(MessageToMapConverter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String EMPTY_STRING = "";

    static Map<String, Object> mapMessage(final @Nonnull BotProfile from, final @Nonnull UserProfile to,
                                          final @Nonnull Message message, final @Nonnull Optional<MessageKeyboard> optionalKeyboard,
                                          final @Nonnull Optional<TrackingData> optionalTrackingData) {

        final Map<String, Object> messageMap = message.getMapRepresentation();

        messageMap.put("tracking_data", isPresentAndNotEmpty(optionalTrackingData) ? serializeTrackingData(optionalTrackingData.get()) : null);
        messageMap.put("keyboard", isPresentAndNotEmpty(optionalKeyboard) ? optionalKeyboard.get() : null);

        return new HashMap<String, Object>() {{
            put("receiver", to.getId());
            put("sender", new HashMap<String, Object>() {{
                put("name", from.getName());
                put("avatar", MoreObjects.firstNonNull(from.getAvatar(), ""));
            }});
            putAll(messageMap);
        }};
    }

    private static String serializeTrackingData(final TrackingData trackingData) {
        try {
            return objectMapper.writeValueAsString(trackingData);
        } catch (final Exception e) {
            logger.warn("Could not serialize tracking data", trackingData);
            return EMPTY_STRING;
        }
    }

    private static <T extends Map> boolean isPresentAndNotEmpty(final Optional<T> optional) {
        return optional.isPresent() && !optional.get().isEmpty();
    }
}
