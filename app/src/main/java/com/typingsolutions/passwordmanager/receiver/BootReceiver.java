package com.typingsolutions.passwordmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.typingsolutions.passwordmanager.services.LoginService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, LoginService.class);
        context.startService(serviceIntent);
    }
}
