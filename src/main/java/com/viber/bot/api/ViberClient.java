package com.viber.bot.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.viber.bot.ViberEnvironmentConfiguration;
import com.viber.bot.event.Event;
import com.viber.bot.message.Message;
import com.viber.bot.message.MessageKeyboard;
import com.viber.bot.message.TrackingData;
import com.viber.bot.profile.BotProfile;
import com.viber.bot.profile.UserProfile;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.viber.bot.Preconditions.checkNotEmpty;

@ThreadSafe
class ViberClient {

    static final String VIBER_AUTH_TOKEN_HEADER = "X-Viber-Auth-Token";
    static final String USER_AGENT_HEADER_FIELD = "User-Agent";
    static final String USER_AGENT_HEADER_VALUE = "ViberBot-Java/";
    static final String VIBER_LIBRARY_VERSION = "1.0.8";

    private static final String STATUS = "status";
    private static final int MAX_GET_ONLINE_IDS = 100;

    private static final Logger logger = LoggerFactory.getLogger(ViberClient.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ListeningExecutorService ioThreadPool = ViberEnvironmentConfiguration.getExecutorService();

    private final String authToken;
    private final String apiUrl;
    private final String userAgent;

    ViberClient(final @Nonnull String apiUrl, final @Nonnull String authToken) {
        this.apiUrl = checkNotEmpty(apiUrl);
        this.authToken = checkNotEmpty(authToken);
        this.userAgent = String.format("%s%s", USER_AGENT_HEADER_VALUE, VIBER_LIBRARY_VERSION);
    }

    ListenableFuture<ApiResponse> setWebhook(final @Nullable String url, final @Nonnull Collection<Event> events) {
        return sendRequest(Endpoint.SET_WEBHOOK, new HashMap<String, Object>() {{
            put("url", url);
            put("event_types", events.stream().map(Event::getServerEventName).filter(Objects::nonNull).collect(Collectors.toList()));
        }});
    }

    ListenableFuture<ApiResponse> sendMessage(final @Nonnull BotProfile from, final @Nonnull UserProfile to,
                                              final @Nonnull Message message, final @Nonnull Optional<MessageKeyboard> optionalKeyboard,
                                              final @Nonnull Optional<TrackingData> optionalTrackingData) {
        return sendRequest(Endpoint.SEND_MESSAGE, MessageToMapConverter.mapMessage(from, to, message, optionalKeyboard, optionalTrackingData));
    }

    ListenableFuture<ApiResponse> getAccountInfo() {
        return sendRequest(Endpoint.GET_ACCOUNT_INFO, Maps.newHashMap());
    }

    ListenableFuture<ApiResponse> getUserDetails(final @Nonnull String userId) {
        return sendRequest(Endpoint.GET_USER_DETAILS, new HashMap<String, Object>() {{
            put("id", checkNotEmpty(userId));
        }});
    }

    ListenableFuture<ApiResponse> getOnlineStatus(final @Nonnull Collection<String> userIds) {
        checkState(!userIds.isEmpty() && userIds.size() < MAX_GET_ONLINE_IDS, String.format("Maximum %d user ids per request are allowed", MAX_GET_ONLINE_IDS));
        return sendRequest(Endpoint.GET_ONLINE_STATUS, new HashMap<String, Object>() {{
            put("ids", userIds);
        }});
    }

    @VisibleForTesting
    Request createRequest(@Nonnull Endpoint endpoint, RequestBody body) {
        return new Request.Builder()
                .url(String.format("%s%s", apiUrl, endpoint.uri))
                .header(VIBER_AUTH_TOKEN_HEADER, authToken)
                .header(USER_AGENT_HEADER_FIELD, userAgent)
                .post(body)
                .build();
    }

    private ListenableFuture<ApiResponse> sendRequest(final @Nonnull Endpoint endpoint, final @Nonnull Map<String, Object> parameters) {
        parameters.put("auth_token", authToken);

        final String json;
        try {
            json = objectMapper.writeValueAsString(checkNotNull(parameters));
        } catch (final Exception e) {
            return Futures.immediateFailedFuture(e);
        }

        final RequestBody body = RequestBody.create(JSON, json);
        final Request request = createRequest(endpoint, body);

        logger.debug("Sending request {}: {}", request, json);

        return ioThreadPool.submit(() -> {
            final Response response = httpClient.newCall(request).execute();
            final String responseBody = response.body().string();
            logger.debug("Got response for request {}: {}", request, responseBody);

            final Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            if (isResponseFailed(responseMap)) throw new ApiException(responseMap);
            else return new ApiResponse(responseMap);
        });
    }

    private boolean isResponseFailed(final Map<String, Object> responseMap) {
        return Integer.parseInt(responseMap.get(STATUS).toString()) != 0;
    }

    @VisibleForTesting
    enum Endpoint {
        SET_WEBHOOK("/set_webhook"),
        SEND_MESSAGE("/send_message"),
        GET_ACCOUNT_INFO("/get_account_info"),
        GET_USER_DETAILS("/get_user_details"),
        GET_ONLINE_STATUS("/get_online");

        private final String uri;

        Endpoint(final @Nonnull String uri) {
            this.uri = checkNotNull(uri);
        }
    }
}
