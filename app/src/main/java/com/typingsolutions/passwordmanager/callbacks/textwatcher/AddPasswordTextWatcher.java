package com.typingsolutions.passwordmanager.callbacks.textwatcher;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;

public class AddPasswordTextWatcher implements TextWatcher {

    private PasswordDetailActivity passwordDetailActivity;
    private String preValue;
    private boolean update;
    private boolean checkHasText;


    public AddPasswordTextWatcher(PasswordDetailActivity passwordDetailActivity, @Nullable String preValue, boolean checkHasText) {
        this.passwordDetailActivity = passwordDetailActivity;
        this.preValue = preValue;
        this.checkHasText = checkHasText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        update = !s.toString().equals(preValue);
        if (update) {
            if(checkHasText) {
                passwordDetailActivity.switchMenuState(s.length() > 0);
                update = update & s.length() > 0;
            } else {
                passwordDetailActivity.switchMenuState(true);
            }
        } else {
            passwordDetailActivity.switchMenuState(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public boolean needUpdate() {
        return update;
    }
}
