package com.typingsolutions.passwordmanager.callbacks.textwatcher;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.callbacks.BaseCallback;
import com.typingsolutions.passwordmanager.callbacks.CreateUserCallback;

import java.lang.reflect.InvocationTargetException;

public class SimpleSwitchTextWatcher implements TextWatcher {
    private Context context;
    private LoginActivity loginActivity;
    private BaseCallback commitCallback;


    public SimpleSwitchTextWatcher(Context context, LoginActivity loginActivity, Class<? extends BaseCallback> commitCallbackClass) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        this.context = context;
        this.loginActivity = loginActivity;
        // What if 'getDeclaredConstructors()[0]' returns null? Exception? Should handle this
        this.commitCallback = (BaseCallback) commitCallbackClass.getDeclaredConstructors()[0].newInstance(context, loginActivity);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count == 0) {
            loginActivity.switchStateOfFloatingActionButton(R.drawable.add, new CreateUserCallback(context));
        } else {
            commitCallback.setValues(s.toString());
            loginActivity.switchStateOfFloatingActionButton(R.drawable.send, commitCallback);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
