package com.viber.bot.api;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ApiException extends ExecutionException {
    private static final String STATUS_MESSAGE = "status_message";

    public ApiException(final Map<String, Object> responseMap) {
        super(responseMap.get(STATUS_MESSAGE).toString());
    }
}
