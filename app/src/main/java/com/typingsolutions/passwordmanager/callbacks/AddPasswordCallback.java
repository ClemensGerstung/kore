package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.view.View;
import android.content.Intent;
import com.typingsolutions.passwordmanager.activities.CreatePasswordActivity;


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
