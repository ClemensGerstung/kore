package com.typingsolutions.passwordmanager.callbacks.service;

import android.os.RemoteException;
import android.support.annotation.Nullable;
import core.IServiceCallback;
import ui.OutlinedImageView;

public class GetLockTimeServiceCallback extends IServiceCallback.Stub {

    private OutlinedImageView view;

    public GetLockTimeServiceCallback(@Nullable OutlinedImageView view) {
        this.view = view;
    }

    @Override
    public void getLockTime(int time, int completeTime) throws RemoteException {
        view.update(time, completeTime);
    }
}
