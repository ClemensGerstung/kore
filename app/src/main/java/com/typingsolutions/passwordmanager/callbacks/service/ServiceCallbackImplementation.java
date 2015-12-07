package com.typingsolutions.passwordmanager.callbacks.service;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import core.IServiceCallback;
import ui.OutlinedImageView;

public class ServiceCallbackImplementation extends IServiceCallback.Stub {
  private LoginActivity loginActivity;

  public ServiceCallbackImplementation(@NonNull LoginActivity loginActivity) {
    this.loginActivity = loginActivity;
  }

  @Override
  public void getLockTime(int time, int completeTime) throws RemoteException {
    final OutlinedImageView background = loginActivity.getBackground();
    background.update(time, completeTime);

    loginActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        background.invalidate();
      }
    });
  }

  @Override
  public void onStart() throws RemoteException {
    loginActivity.hideInput();
  }

  @Override
  public void onFinish() throws RemoteException {
    loginActivity.showInput();
  }
}
