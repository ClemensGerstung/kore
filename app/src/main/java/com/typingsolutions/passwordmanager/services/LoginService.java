package com.typingsolutions.passwordmanager.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.util.Log;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import com.typingsolutions.passwordmanager.IServiceCallback;


public class LoginService extends Service {

  private static final int SLEEP_TIME = 1000;

  private final RemoteCallbackList<IServiceCallback> mCallbacks = new RemoteCallbackList<>();

  private int mTries;
  private int mCurrentLockTime;
  private int mCurrentMaxLockTime;
  private volatile Looper mServiceLooper;
  private volatile ServiceHandler mServiceHandler;

  private final ILoginServiceRemote.Stub mBinder = new LoginServiceBinder();

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
    Log.d(getClass().getSimpleName(), "onBind");

    if (mTries != 0) {
      Message msg = mServiceHandler.obtainMessage();
      mServiceHandler.sendMessage(msg);
    }

    return mBinder;
  }

  @Override
  public boolean onUnbind(Intent intent) {
    Log.d(getClass().getSimpleName(), "onUnbind");
    return true;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(getClass().getSimpleName(), "onStartCommand");
    int startCommand = super.onStartCommand(intent, flags, startId);

    SharedPreferences prefs = getSharedPreferences(getClass().getSimpleName(), MODE_PRIVATE);
    mTries = prefs.getInt("mTries", 0);
    mCurrentLockTime = prefs.getInt("mCurrentLockTime", 0);
    mCurrentMaxLockTime = prefs.getInt("mCurrentMaxLockTime", 0);

    prefs.edit().clear().apply();

    if (mTries == 0 && intent == null) {
      startCommand = START_NOT_STICKY;
      stopSelf();
    }


    return startCommand;
  }

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    super.onTaskRemoved(rootIntent);
    Log.d(getClass().getSimpleName(), "onTaskRemoved");
    SharedPreferences.Editor editor = getSharedPreferences(getClass().getSimpleName(), MODE_PRIVATE).edit();
    editor.putInt("mTries", mTries);
    editor.putInt("mCurrentLockTime", mCurrentLockTime);
    editor.putInt("mCurrentMaxLockTime", mCurrentMaxLockTime);
    editor.apply();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mServiceLooper.quit();
    mCallbacks.kill();
    Log.d(getClass().getSimpleName(), "onDestroy");
  }

  private class ServiceHandler extends Handler {
    ServiceHandler(Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
      try {
        long lastTime = SystemClock.elapsedRealtime();

        int size = mCallbacks.beginBroadcast();

        for (int i = 0; i < size; i++)
          mCallbacks.getBroadcastItem(i).onStart();

        mCallbacks.finishBroadcast();

        do {
          long time = SystemClock.elapsedRealtime();
          long diff = time - lastTime;
          lastTime = time;

          mCurrentLockTime -= diff;

          size = mCallbacks.beginBroadcast();

          for (int i = 0; i < size; i++) {
            mCallbacks.getBroadcastItem(i).getLockTime(mCurrentLockTime, mCurrentMaxLockTime);
          }

          mCallbacks.finishBroadcast();

          SystemClock.sleep(SLEEP_TIME);
        } while (mCurrentLockTime > 0);

        size = mCallbacks.beginBroadcast();

        for (int i = 0; i < size; i++)
          mCallbacks.getBroadcastItem(i).onFinish();

        mCallbacks.finishBroadcast();
      } catch (RemoteException e) {
        Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
      }
    }
  }

  private class LoginServiceBinder extends ILoginServiceRemote.Stub {
    @Override
    public void increaseTries() throws RemoteException {
      mTries++;
      boolean start = false;
      if (mTries == 3) {
        mCurrentMaxLockTime = 30 * 1000;
        mCurrentLockTime = mCurrentMaxLockTime;
        start = true;
      } else if (mTries == 6) {
        mCurrentMaxLockTime = 60 * 1000;
        mCurrentLockTime = mCurrentMaxLockTime;
        start = true;
      } else if (mTries == 9) {
        mCurrentMaxLockTime = 15 * 1000;
        mCurrentLockTime = mCurrentMaxLockTime;
        start = true;
      } else if (mTries == 12) {
        mCurrentMaxLockTime = 30 * 1000;
        mCurrentLockTime = mCurrentMaxLockTime;
        start = true;
      } else if (mTries > 12 && mTries % 12 == 0) {
        mCurrentMaxLockTime = 30 * 1000;
        mCurrentLockTime = mCurrentMaxLockTime;
        start = true;
      }

      if (start) {
        Message msg = mServiceHandler.obtainMessage();
        mServiceHandler.sendMessage(msg);
      }
    }

    @Override
    public void resetTries() throws RemoteException {
      mTries = 0;
    }

    @Override
    public int getRemainingTries() throws RemoteException {
      if (mTries < 3) {
        return 3 - mTries - 1;
      } else if (mTries < 6) {
        return 6 - mTries - 1;
      } else if (mTries < 9) {
        return 9 - mTries - 1;
      } else if (mTries < 12) {
        return 12 - mTries - 1;
      } else if (mTries >= 12) {
        return 1;
      }
      return 0;
    }

    @Override
    public boolean isBlocked() throws RemoteException {
      return mCurrentLockTime > 0;
    }

    @Override
    public void stop() throws RemoteException {
      stopSelf();
    }


    @Override
    public void registerCallback(IServiceCallback callback) throws RemoteException {
      if (callback != null) {
        mCallbacks.register(callback);
      }
    }

    @Override
    public void unregisterCallback(IServiceCallback callback) throws RemoteException {
      if (callback != null) {
        mCallbacks.unregister(callback);
      }
    }
  }
}
