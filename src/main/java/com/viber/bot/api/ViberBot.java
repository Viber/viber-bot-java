package com.viber.bot.api;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.viber.bot.Request;
import com.viber.bot.event.BotEventListener;
import com.viber.bot.event.Event;
import com.viber.bot.event.EventEmitter;
import com.viber.bot.event.callback.*;
import com.viber.bot.message.Message;
import com.viber.bot.message.TextMessage;
import com.viber.bot.middleware.DefaultMiddleware;
import com.viber.bot.middleware.Middleware;
import com.viber.bot.profile.BotProfile;
import com.viber.bot.profile.UserProfile;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.io.InputStream;
import java.util.Collection;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viber.bot.Preconditions.checkNotEmpty;

@ThreadSafe
public class ViberBot {

    private static final String API_URL = "https://chatapi.viber.com/pa";

    private final EventEmitter eventEmitter = new EventEmitter();
    private final RegexMatcherRouter regexMatcherRouter = new RegexMatcherRouter();

    private final ViberClient client;
    private final MessageSender messageSender;
    private final Middleware middleware;

    private final BotProfile botProfile;

    public ViberBot(final @Nonnull BotProfile botProfile, final @Nonnull String authToken) {
        checkNotEmpty(authToken, "empty auth token");
        this.botProfile = checkNotNull(botProfile);

        client = new ViberClient(API_URL, authToken);
        messageSender = new MessageSender(getBotProfile(), client);
        middleware = new DefaultMiddleware(new RequestReceiverImpl(this, eventEmitter));

        setupTextMessageReceivedHandler();
    }

    /**
     * On incoming message from Viber, handles the message asynchronously.
     * <p>
     * This method returns a {@link ListenableFuture} with {@link InputStream} an promise.
     * The returned InputStream should be used as a response to the incoming request from Viber.
     * An InputStream is not guaranteed to be returned, and may be <code>null</code>.
     * The url argument must specify an absolute {@link Request}. The name
     * argument is a specifier that is relative to the url argument.
     *
     * @param request the Request object
     * @return a future with nullable InputStream.
     * @see Request
     */
    public ListenableFuture<InputStream> incoming(final @Nonnull Request request) {
        return middleware.incoming(request);
    }

    /**
     * Sends an set webhook request to Viber
     * The url must start with HTTPS, and must have valid ssl certificate.
     *
     * @param url the url to register as webhook
     * @return a future with {@link ApiResponse} that may throw {@link ApiException}.
     * @see ApiResponse
     * @see ApiException
     */
    public ListenableFuture<ApiResponse> setWebhook(final @Nonnull String url) {
        return client.setWebhook(url, Lists.newArrayList(Event.values()));
    }

    /**
     * Sends messages in order to Viber user (via {@link UserProfile}).
     * <p>
     * This method ensures messages are sent in the order they are received.
     * It will discard all keyboard in all messages expect the last message.
     *
     * @param to       {@link UserProfile}
     * @param messages collection of {@link Message}
     * @return future which contains a collection of message tokens as strings, that may throw {@link ApiException}.
     * @see Message
     * @see UserProfile
     * @see ApiException
     */
    public ListenableFuture<Collection<String>> sendMessage(final @Nonnull UserProfile to, final @Nonnull Collection<Message> messages) {
        final ListenableFuture<Collection<String>> messageTokens = messageSender.sendMessage(to, messages);
        messageTokens.addListener(() -> messages.forEach(message -> eventEmitter.emit(Event.MESSAGE_SENT, to, message)), Runnable::run);
        return messageTokens;
    }

    /**
     * Sends one or more messages to a Viber user.
     * Shorthand for {@link ViberBot#sendMessage(UserProfile, Collection)}
     *
     * @param to       {@link UserProfile} to send messages to
     * @param messages one or more messages to send
     * @return future which contains a collection of message tokens as strings, that may throw {@link ApiException}.
     * @see ViberBot#sendMessage(UserProfile, Collection)
     */
    public ListenableFuture<Collection<String>> sendMessage(final @Nonnull UserProfile to, final @Nonnull Message... messages) {
        return sendMessage(to, Lists.newArrayList(messages));
    }

    /**
     * Returns the BOT public account info.
     *
     * @return a future with {@link ApiResponse} that may throw {@link ApiException}.
     * @see ApiResponse
     * @see ApiException
     */
    public ListenableFuture<ApiResponse> getAccountInfo() {
        return client.getAccountInfo();
    }

    /**
     * Returns the BOT profile.
     *
     * @return {@link BotProfile}
     */
    public BotProfile getBotProfile() {
        return botProfile;
    }

    /**
     * Returns user details of a specific Viber user.
     *
     * @param userId a string representing the user id, can be obtained from {@link UserProfile#getId}
     * @return a future with {@link ApiResponse} that may throw {@link ApiException}.
     * @see ApiResponse
     * @see ApiException
     */
    public ListenableFuture<ApiResponse> getUserDetails(final @Nonnull String userId) {
        return client.getUserDetails(userId);
    }

    /**
     * Registers a callback for event {@link Event#MESSAGE_RECEIVED}.
     * The listener will be called upon {@link ViberBot#incoming(Request)} of that event.
     *
     * @param listener an {@link OnMessageReceived} listener
     * @see OnMessageReceived
     */
    public void onMessageReceived(final @Nonnull OnMessageReceived listener) {
        on(Event.MESSAGE_RECEIVED, listener);
    }

    /**
     * Registers a callback for event {@link Event#MESSAGE_SENT}.
     * The listener will be called when {@link ViberBot#sendMessage} successfully sends a message.
     *
     * @param listener an {@link OnMessageSent} listener
     * @see OnMessageSent
     */
    public void onMessageSent(final @Nonnull OnMessageSent listener) {
        on(Event.MESSAGE_SENT, listener);
    }

    /**
     * Registers a callback for a regular expression {@link TextMessage}.
     * When a TextMessage arrives, will call listeners with matching pattern sequentially
     * Call ordering is not gurenteed.
     *
     * @param pattern  {@link Pattern}
     * @param listener an {@link OnMessageReceived} listener
     * @see OnMessageReceived
     */
    public void onTextMessage(final @Nonnull Pattern pattern, final @Nonnull OnMessageReceived listener) {
        regexMatcherRouter.newMatcher(pattern, listener);
    }

    /**
     * Shorthand for {@link ViberBot#onTextMessage(Pattern, OnMessageReceived)}
     * Creates a case insensitive {@link Pattern} object.
     *
     * @param regex    regular expression
     * @param listener an {@link OnMessageReceived} listener
     * @see OnMessageReceived
     * @see ViberBot#onTextMessage(Pattern, OnMessageReceived)
     */
    public void onTextMessage(final @Nonnull String regex, final @Nonnull OnMessageReceived listener) {
        regexMatcherRouter.newMatcher(Pattern.compile(regex, Pattern.CASE_INSENSITIVE), listener);
    }

    /**
     * Registers a callback for event {@link Event#MESSAGE_SEEN}.
     * The listener will be called upon {@link ViberBot#incoming(Request)} of that event.
     *
     * @param listener an {@link OnMessageSeen} listener
     * @see OnMessageSeen
     */
    public void onMessageSeen(final @Nonnull OnMessageSeen listener) {
        on(Event.MESSAGE_SEEN, listener);
    }

    /**
     * Registers a callback for event {@link Event#MESSAGE_DELIVERED}.
     * The listener will be called upon {@link ViberBot#incoming(Request)} of that event.
     *
     * @param listener an {@link OnMessageDelivered} listener
     * @see OnMessageDelivered
     */
    public void onMessageDelivered(final @Nonnull OnMessageDelivered listener) {
        on(Event.MESSAGE_DELIVERED, listener);
    }

    /**
     * Registers a callback for event {@link Event#CONVERSATION_STARTED}.
     * The listener will be called upon {@link ViberBot#incoming(Request)} of that event.
     *
     * @param listener an {@link OnConversationStarted} listener
     * @see OnConversationStarted
     */
    public void onConversationStarted(final @Nonnull OnConversationStarted listener) {
        on(Event.CONVERSATION_STARTED, listener);
    }

    /**
     * Registers a callback for event {@link Event#SUBSCRIBED}.
     * The listener will be called upon {@link ViberBot#incoming(Request)} of that event.
     *
     * @param listener an {@link OnSubscribe} listener
     * @see OnSubscribe
     */
    public void onSubscribe(final @Nonnull OnSubscribe listener) {
        on(Event.SUBSCRIBED, listener);
    }

    /**
     * Registers a callback for event {@link Event#UNSUBSCRIBED}.
     * The listener will be called upon {@link ViberBot#incoming(Request)} of that event.
     *
     * @param listener an {@link OnUnsubscribe} listener
     * @see OnUnsubscribe
     */
    public void onUnsubscribe(final @Nonnull OnUnsubscribe listener) {
        on(Event.UNSUBSCRIBED, listener);
    }

    private void on(final @Nonnull Event event, final @Nonnull BotEventListener listener) {
        eventEmitter.on(event, listener);
    }

    private void setupTextMessageReceivedHandler() {
        on(Event.MESSAGE_RECEIVED, (OnMessageReceived) (event, message, response) -> {
            if (!(message instanceof TextMessage)) return;
            final TextMessage textMessage = (TextMessage) event.getMessage();
            regexMatcherRouter.tryGetCallback(textMessage.getText()).forEach(callback -> callback.emit(event, message, response));
        });
    }
}
