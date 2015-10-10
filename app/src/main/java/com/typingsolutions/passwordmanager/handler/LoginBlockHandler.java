package com.typingsolutions.passwordmanager.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import com.typingsolutions.passwordmanager.services.LoginService;
import core.login.BlockedUser;

public class LoginBlockHandler extends Handler {
    private Context context;
    private BlockedUser user;

    public LoginBlockHandler(Context context, BlockedUser user) {
        this.context = context;
        this.user = user;
    }

    public LoginBlockHandler(Context context, BlockedUser user, Callback callback) {
        super(callback);
        this.context = context;
        this.user = user;
    }

    public LoginBlockHandler(Context context, BlockedUser user, Looper looper) {
        super(looper);
        this.context = context;
        this.user = user;
    }

    public LoginBlockHandler(Context context, BlockedUser user, Looper looper, Callback callback) {
        super(looper, callback);
        this.context = context;
        this.user = user;
    }

    @Override
    public void handleMessage(Message msg) {

        long lastSystemTime = SystemClock.elapsedRealtime();

        Intent intent = new Intent(LoginService.INTENT_ACTION);

        do {
            long currentSystemTime = SystemClock.elapsedRealtime();
            int subtract = (int) (currentSystemTime - lastSystemTime);
            lastSystemTime = currentSystemTime;
            user.reduceTimeRemaining(subtract);
            context.getApplicationContext().sendBroadcast(intent);

            Log.d(getClass().getSimpleName(), Integer.toString(user.getTimeRemaining()));

            SystemClock.sleep(LoginService.SLEEP_TIME);
        } while (user.getTimeRemaining() > 0);

    }
}
