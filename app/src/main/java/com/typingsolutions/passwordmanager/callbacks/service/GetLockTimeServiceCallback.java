package com.typingsolutions.passwordmanager.callbacks.service;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public class GetLockTimeServiceCallback extends BaseServiceCallback {

    public GetLockTimeServiceCallback(@NonNull Context context, @Nullable View view) {
        super(context, view);
    }

    @Override
    public void getLockTime(int id, int time, int maxTime) throws RemoteException {

    }
}
