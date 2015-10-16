package com.typingsolutions.passwordmanager.callbacks.textwatcher;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;

public class AddPasswordTextWatcher implements TextWatcher {

    private PasswordDetailActivity passwordDetailActivity;
    private String preValue;

    public AddPasswordTextWatcher(PasswordDetailActivity passwordDetailActivity, @Nullable String preValue) {
        this.passwordDetailActivity = passwordDetailActivity;
        this.preValue = preValue;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals(preValue)) {
            passwordDetailActivity.switchMenuState(s.length() > 0);
        } else {
            passwordDetailActivity.switchMenuState(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
