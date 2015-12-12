package com.typingsolutions.passwordmanager.callbacks.click;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.typingsolutions.passwordmanager.activities.CreatePasswordActivity;
import com.typingsolutions.passwordmanager.callbacks.BaseCallback;


public class AddPasswordCallback extends BaseCallback {
    public AddPasswordCallback(Context context) {
        super(context);
    }

    @Override
    public void setValues(Object... values) {
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, CreatePasswordActivity.class);
        context.startActivity(intent);
    }
}
