package com.typingsolutions.passwordmanager.callbacks;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.IServiceCallback;
import ui.OutlinedImageView;

public class ServiceCallbackImplementation extends IServiceCallback.Stub {
  private LoginActivity mActivity;

  public ServiceCallbackImplementation(@NonNull LoginActivity loginActivity) {
    this.mActivity = loginActivity;
  }

  @Override
  public void getLockTime(int time, int completeTime) throws RemoteException {
    final OutlinedImageView background = mActivity.getBackground();
    background.update(time, completeTime);

    mActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        background.invalidate();
      }
    });
  }

  @Override
  public void onStart() throws RemoteException {
    mActivity.hideInput();
  }
  @Override
  public void onFinish() throws RemoteException {
    mActivity.showInput();
  }
}
