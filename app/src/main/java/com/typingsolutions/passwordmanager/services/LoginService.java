package com.typingsolutions.passwordmanager.services;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import com.typingsolutions.passwordmanager.IServiceCallback;


public class LoginService extends Service {

  private static final int SLEEP_TIME = 1000;

  private final RemoteCallbackList<IServiceCallback> callbacks = new RemoteCallbackList<>();

  private int tries;
  private int currentLockTime;
  private int currentMaxLockTime;
  private volatile Looper mServiceLooper;
  private volatile ServiceHandler mServiceHandler;

  private final ILoginServiceRemote.Stub binder = new LoginServiceBinder();

  @Override
  public void onCreate() {
    super.onCreate();
    HandlerThread thread = new HandlerThread("IntentService[" + "LoginThread" + "]");
    thread.start();

    mServiceLooper = thread.getLooper();
    mServiceHandler = new ServiceHandler(mServiceLooper);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  @Override
  public boolean onUnbind(Intent intent) {
    return true;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mServiceLooper.quit();
  }

  private class ServiceHandler extends Handler {
    ServiceHandler(Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
      try {
        long lastTime = SystemClock.elapsedRealtime();

        int size = callbacks.beginBroadcast();

        for (int i = 0; i < size; i++)
          callbacks.getBroadcastItem(i).onStart();

        callbacks.finishBroadcast();

        do {
          long time = SystemClock.elapsedRealtime();
          long diff = time - lastTime;
          lastTime = time;

          currentLockTime -= diff;

          size = callbacks.beginBroadcast();

          for (int i = 0; i < size; i++) {
            callbacks.getBroadcastItem(i).getLockTime(currentLockTime, currentMaxLockTime);
          }

          callbacks.finishBroadcast();

          SystemClock.sleep(SLEEP_TIME);
        } while (currentLockTime > 0);

        size = callbacks.beginBroadcast();

        for (int i = 0; i < size; i++)
          callbacks.getBroadcastItem(i).onFinish();

        callbacks.finishBroadcast();
      } catch (RemoteException e) {
        Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
      }
    }
  }

  private class LoginServiceBinder extends ILoginServiceRemote.Stub {
    @Override
    public void increaseTries() throws RemoteException {
      tries++;
      boolean start = false;
      if (tries == 3) {
        currentMaxLockTime = 30 * 1000;
        currentLockTime = currentMaxLockTime;
        start = true;
      } else if (tries == 6) {
        currentMaxLockTime = 60 * 1000;
        currentLockTime = currentMaxLockTime;
        start = true;
      } else if (tries == 9) {
        currentMaxLockTime = 15 * 1000;
        currentLockTime = currentMaxLockTime;
        start = true;
      } else if (tries == 12) {
        currentMaxLockTime = 30 * 1000;
        currentLockTime = currentMaxLockTime;
        start = true;
      } else if (tries > 12 && tries % 12 == 0) {
        currentMaxLockTime = 30 * 1000;
        currentLockTime = currentMaxLockTime;
        start = true;
      }

      if (start) {
        Message msg = mServiceHandler.obtainMessage();
        mServiceHandler.sendMessage(msg);
      }
    }

    @Override
    public void resetTries() throws RemoteException {
      tries = 0;
    }

    @Override
    public int getRemainingTries() throws RemoteException {
      if (tries < 3) {
        return 3 - tries - 1;
      } else if (tries < 6) {
        return 6 - tries - 1;
      } else if (tries < 9) {
        return 9 - tries - 1;
      } else if (tries < 12) {
        return 12 - tries - 1;
      } else if (tries >= 12) {
        return 1;
      }
      return 0;
    }

    @Override
    public boolean isBlocked() throws RemoteException {
      return currentLockTime > 0;
    }

    @Override
    public void stop() throws RemoteException {
      stopSelf();
    }


    @Override
    public void registerCallback(IServiceCallback callback) throws RemoteException {
      if (callback != null) {
      callbacks.register(callback);
      }
    }

    @Override
    public void unregisterCallback(IServiceCallback callback) throws RemoteException {
      if (callback != null) {
      callbacks.unregister(callback);
      }
    }
  }
}
