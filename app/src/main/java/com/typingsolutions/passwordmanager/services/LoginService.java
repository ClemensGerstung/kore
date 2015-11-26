package com.typingsolutions.passwordmanager.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import core.IServiceCallback;
import core.Utils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public class LoginService extends Service {

  public static final String INTENT_ACTION = "com.typingsolutions.passwordmanager.service.LoginService.UPDATE_BLOCKING";
  public static final String INTENT_BLOCK = "com.typingsolutions.passwordmanager.service.LoginService.BLOCK";

  public static final int SLEEP_TIME = 1000;

  // tries until block
  public static final int TRIES_FOR_SMALL_BLOCK = 3;
  public static final int TRIES_FOR_MEDIUM_BLOCK = 6;
  public static final int TRIES_FOR_LARGE_BLOCK = 9;
  public static final int TRIES_FOR_FINAL_BLOCK = 12;

  // block times in ms
  public static final int SMALL_BLOCK_TIME = 30000;    // 0.5 minutes
  public static final int MEDIUM_BLOCK_TIME = 60000;   // 1 minute
  public static final int LARGE_BLOCK_TIME = 150000;   // 2.5 minutes
  public static final int FINAL_BLOCK_TIME = 300000;   // 5 minutes

  private final RemoteCallbackList<IServiceCallback> callbacks = new RemoteCallbackList<>();

  private int tries;
  private int currentLockTime;
  private int currentMaxLockTime;


  private final Runnable blockRunnable = new Runnable() {
    @Override
    public void run() {
      long lastTime = SystemClock.elapsedRealtime();
      Intent intent = new Intent(INTENT_ACTION);

      do {
        long time = SystemClock.elapsedRealtime();
        long diff = time - lastTime;

        currentLockTime -= diff;
        getApplicationContext().sendBroadcast(intent);

        SystemClock.sleep(SLEEP_TIME);
      } while (currentLockTime > 0);
    }
  };

  private final ILoginServiceRemote.Stub binder = new ILoginServiceRemote.Stub() {
    @Override
    public void increaseTries() throws RemoteException {
      tries++;
      boolean start = false;
      if (tries == TRIES_FOR_SMALL_BLOCK) {
        currentMaxLockTime = SMALL_BLOCK_TIME;
        currentLockTime = currentMaxLockTime;
        start = true;
      } else if (tries == TRIES_FOR_MEDIUM_BLOCK) {
        currentMaxLockTime = MEDIUM_BLOCK_TIME;
        currentLockTime = currentMaxLockTime;
        start = true;
      } else if (tries == TRIES_FOR_LARGE_BLOCK) {
        currentMaxLockTime = LARGE_BLOCK_TIME;
        currentLockTime = currentMaxLockTime;
        start = true;
      } else if (tries == TRIES_FOR_FINAL_BLOCK) {
        currentMaxLockTime = FINAL_BLOCK_TIME;
        currentLockTime = currentMaxLockTime;
        start = true;
      } else if (tries > TRIES_FOR_FINAL_BLOCK && tries % TRIES_FOR_SMALL_BLOCK == 0) {
        currentMaxLockTime = FINAL_BLOCK_TIME;
        currentLockTime = currentMaxLockTime;
        start = true;
      }

      if (start) {
        new Thread(blockRunnable).start();
      }
    }

    @Override
    public void getBlockedTimeAsync() throws RemoteException {
      final int size = callbacks.beginBroadcast();

      for (int i = 0; i < size; i++) {
        callbacks.getBroadcastItem(i).getLockTime(currentLockTime, currentMaxLockTime);
      }

      callbacks.finishBroadcast();
    }

    @Override
    public boolean isUserBlocked() throws RemoteException {
      return currentLockTime > 0;
    }

    @Override
    public int getMaxBlockTime() throws RemoteException {
      return currentMaxLockTime;
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
  };

  @Override
  public IBinder onBind(Intent intent) {
    readSerializedData();
    return binder;
  }

  @Override
  public void onRebind(Intent intent) {
    readSerializedData();
  }

  @Override
  public boolean onUnbind(Intent intent) {
    SharedPreferences preferences = getSharedPreferences(getClass().getSimpleName(), MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();

   /* try {
      String json = blockedUserList.toJson();
      String hash = Utils.getHashedString(json);

//            Log.d(getClass().getSimpleName(), json);

      editor.putString("json", json);
      editor.putString("hash", hash);

      editor.apply();
    } catch (IOException | NoSuchAlgorithmException e) {
      Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
    }*/

    return true;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    readSerializedData();
    return START_STICKY;
  }

  @Override
  public void onDestroy() {

  }

  private void readSerializedData() {
    SharedPreferences preferences = getSharedPreferences(getClass().getSimpleName(), MODE_PRIVATE);
    String json = preferences.getString("json", "");
    String hash = preferences.getString("hash", "");

    /*try {
      String computedHash = Utils.getHashedString(json);
      blockedUserList.fromJson(json, hash.equals(computedHash));
    } catch (NoSuchAlgorithmException | IOException e) {
      Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
    } finally {
      preferences.edit().clear().apply();
    }*/
  }


}
