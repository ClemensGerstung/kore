package com.typingsolutions.passwordmanager.callbacks.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import core.IServiceCallback;

public abstract class BaseServiceCallback extends IServiceCallback.Stub {
    protected View view;
    protected Context context;

    public BaseServiceCallback(@NonNull Context context, @Nullable View view) {
        this.view = view;
        this.context = context;
    }
}
