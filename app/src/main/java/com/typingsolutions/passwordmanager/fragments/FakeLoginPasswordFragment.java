package com.typingsolutions.passwordmanager.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import com.typingsolutions.passwordmanager.callbacks.FakeLoginCallback;
import com.typingsolutions.passwordmanager.callbacks.textwatcher.SimpleSwitchTextWatcher;

public class FakeLoginPasswordFragment extends LoginPasswordFragment {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        password.removeTextChangedListener(watcher);

        try {
            watcher = new SimpleSwitchTextWatcher(loginActivity, loginActivity, FakeLoginCallback.class);
            password.addTextChangedListener(watcher);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
        }
    }
}
