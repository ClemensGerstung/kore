package com.typingsolutions.passwordmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.typingsolutions.passwordmanager.services.LoginService;

public class BlockReceiver extends BroadcastReceiver {
    private LoginService service;

    public BlockReceiver(LoginService service) {
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
