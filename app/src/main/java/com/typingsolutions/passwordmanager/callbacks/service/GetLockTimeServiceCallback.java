package com.typingsolutions.passwordmanager.callbacks.service;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

//        Log.d(getClass().getSimpleName(), String.format("%s %s", time, completeTime));

        if(time < 0)
        {
            loginPasswordFragment.showAllInputs();
        }
        if(time >= completeTime)
        {
            loginPasswordFragment.hideAllInputs();
        }
    }
}
