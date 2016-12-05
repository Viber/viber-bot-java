package com.viber.bot.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.Futures;
import com.viber.bot.Request;
import com.viber.bot.Response;
import com.viber.bot.event.EventEmitter;
import com.viber.bot.event.incoming.*;
import com.viber.bot.message.Message;
import com.viber.bot.middleware.RequestReceiver;
import com.viber.bot.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

class RequestReceiverImpl implements RequestReceiver {

    private static final Logger logger = LoggerFactory.getLogger(RequestReceiverImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final EventEmitter eventEmitter;
    private final ViberBot bot;

    RequestReceiverImpl(final @Nonnull ViberBot bot, final @Nonnull EventEmitter eventEmitter) {
        this.bot = bot;
        this.eventEmitter = checkNotNull(eventEmitter);
    }

    @Override
    public void acceptRequest(final @Nonnull Request request) {
        final IncomingEvent incomingEvent = request.getEvent();
        switch (incomingEvent.getEvent()) {
            case ERROR: {
                logger.error("Error from Viber servers: {}", ((IncomingErrorEvent) request.getEvent()).getDescription());
                break;
            }

            case CONVERSATION_STARTED: {
                final IncomingConversationStartedEvent incomingConversationStartedEvent = (IncomingConversationStartedEvent) request.getEvent();
                eventEmitter.<Optional<Message>>emit(incomingEvent.getEvent(), incomingEvent)
                        .forEach(setReturnedResponse(request, incomingConversationStartedEvent.getUser()));
                break;
            }

            case MESSAGE_RECEIVED: {
                final IncomingMessageEvent incomingMessageEvent = (IncomingMessageEvent) request.getEvent();
                final Response response = new Response(incomingMessageEvent.getSender(), bot);
                eventEmitter.emit(incomingEvent.getEvent(), incomingEvent, incomingMessageEvent.getMessage(), response);
                break;
            }

            case SUBSCRIBED: {
                final IncomingSubscribedEvent incomingSubscribedEvent = (IncomingSubscribedEvent) request.getEvent();
                final Response response = new Response(incomingSubscribedEvent.getUser(), bot);
                eventEmitter.emit(incomingEvent.getEvent(), incomingEvent, response);
                break;
            }

            default: {
                eventEmitter.emit(incomingEvent.getEvent(), incomingEvent);
                break;
            }
        }
    }

    private Consumer<Future<Optional<Message>>> setReturnedResponse(final Request request, final UserProfile userProfile) {
        return messageFuture -> {

            final Optional<Message> message = Futures.getUnchecked(messageFuture);
            if (!message.isPresent()) return;

            try {
                final String json = objectMapper.writeValueAsString(getMessageMapping(userProfile, message.get()));
                request.setResponse(json);
            } catch (final Exception e) {
                logger.error("Could not send back response to conversation started event", e);
            }
        };
    }

    private Map<String, Object> getMessageMapping(final UserProfile userProfile, final Message message) {
        return MessageToMapConverter.mapMessage(
                bot.getBotProfile(), userProfile, message,
                Optional.ofNullable(message.getKeyboard()),
                Optional.ofNullable(message.getTrackingData()));
    }
}
