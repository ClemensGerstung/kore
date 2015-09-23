package com.typingsolutions.passwordmanager.callbacks;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.typingsolutions.passwordmanager.activities.CreateUserActivity;

public class CreateUserCallback extends BaseCallback {

    public static final String ADD_USER_CALLBACK_INTENT_NAME = "com.typingsolutions.passwordmanager.callbacks.CreateUserCallback.name";

    private String name;

    public CreateUserCallback(Context context) {
        this(context, null);
    }

    @Override
    public void setValues(Object... values) {

    }

    public CreateUserCallback(Context context, String name) {
        super(context);
        this.name = name;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, CreateUserActivity.class);
        intent.putExtra(ADD_USER_CALLBACK_INTENT_NAME, name);
        context.startActivity(intent);
    }
}
