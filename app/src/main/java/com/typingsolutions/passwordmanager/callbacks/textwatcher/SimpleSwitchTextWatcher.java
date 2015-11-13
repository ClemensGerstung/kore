package com.typingsolutions.passwordmanager.callbacks.textwatcher;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.callbacks.BaseCallback;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SimpleSwitchTextWatcher implements TextWatcher {
    private Context context;
    private LoginActivity loginActivity;
    private BaseCallback commitCallback;


    public SimpleSwitchTextWatcher(Context context, LoginActivity loginActivity, Class<? extends BaseCallback> commitCallbackClass) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        this.context = context;
        this.loginActivity = loginActivity;

        Constructor<?> constructor = commitCallbackClass.getDeclaredConstructors()[0];
        if(constructor == null) return;
        this.commitCallback = (BaseCallback) constructor.newInstance(context, loginActivity);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {

        } else {
            commitCallback.setValues(s.toString());
            loginActivity.switchStateOfFloatingActionButton(R.mipmap.send, commitCallback);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
