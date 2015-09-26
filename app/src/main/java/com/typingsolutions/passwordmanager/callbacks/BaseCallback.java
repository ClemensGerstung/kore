package com.typingsolutions.passwordmanager.callbacks;


import android.content.Context;
import android.view.View;

public abstract class BaseCallback implements View.OnClickListener {
    protected Context context;

    public BaseCallback(Context context) {
        this.context = context;
    }

    public abstract void setValues(Object... values);
}
