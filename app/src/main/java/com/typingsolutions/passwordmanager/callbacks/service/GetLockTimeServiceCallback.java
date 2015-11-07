package com.typingsolutions.passwordmanager.callbacks.service;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import com.typingsolutions.passwordmanager.fragments.LoginPasswordFragment;
import core.IServiceCallback;
import ui.OutlinedImageView;

public class GetLockTimeServiceCallback extends IServiceCallback.Stub {
    private LoginPasswordFragment loginPasswordFragment;

    public GetLockTimeServiceCallback(@NonNull LoginPasswordFragment loginPasswordFragment) {
        this.loginPasswordFragment = loginPasswordFragment;
    }

    @Override
    public void getLockTime(int time, int completeTime) throws RemoteException {
        OutlinedImageView background = loginPasswordFragment.getBackground();
        background.update(time, completeTime);
//        background.invalidate();

        Log.d(getClass().getSimpleName(), String.format("Update: %s", time));

        if (time <= 0) {
            loginPasswordFragment.showAllInputs();
        } else {
            loginPasswordFragment.hideAllInputs();
        }
    }
}
