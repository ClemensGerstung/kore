package com.typingsolutions.passwordmanager.callbacks.service;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import core.IServiceCallback;

public class GetLockTimeServiceCallback extends IServiceCallback.Stub {
    private LoginActivity loginActivity;

    public GetLockTimeServiceCallback(@NonNull LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @Override
    public void getLockTime(int time, int completeTime) throws RemoteException {
//        OutlinedImageView background = loginActivity.getBackground();
//        background.update(time, completeTime);
//        loginActivity.redrawBlockedbackground();
//
//        Log.d(getClass().getSimpleName(), String.format("Update: %s", time));
//
//        if (time <= 0) {
//            loginActivity.showAllInputs();
//        } else {
//            loginActivity.hideAllInputs();
//        }
    }
}
