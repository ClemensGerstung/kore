package com.typingsolutions.passwordmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;

public class WrongPasswordReceiver extends BroadcastReceiver {
    private PasswordOverviewActivity activity;

    public WrongPasswordReceiver(PasswordOverviewActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        activity.makeSnackBar();
    }
}
