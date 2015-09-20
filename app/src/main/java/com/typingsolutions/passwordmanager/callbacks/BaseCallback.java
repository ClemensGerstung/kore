package com.typingsolutions.passwordmanager.callbacks;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public abstract class BaseCallback implements View.OnClickListener {
    Context context;

    public BaseCallback(Context context) {
        this.context = context;
    }

    public abstract void setValues(Object... values);
}
