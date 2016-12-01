package com.viber.bot.event.callback;

import com.viber.bot.event.BotEventListener;
import com.viber.bot.message.Message;
import com.viber.bot.profile.UserProfile;

import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkArgument;

public interface OnMessageSent extends BotEventListener<Void> {
    void messageSent(UserProfile to, Message message);

    @Override
    default Future<Void> emit(final Object... args) {
        checkArgument(args.length == 2);
        messageSent((UserProfile) args[0], (Message) args[1]);
        return nothing;
    }
}