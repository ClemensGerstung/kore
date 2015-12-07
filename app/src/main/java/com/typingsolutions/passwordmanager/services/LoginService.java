package com.typingsolutions.passwordmanager.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import core.IServiceCallback;
import core.Utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
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
        lastTime = time;

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
    load();
    return binder;
  }

  @Override
  public void onRebind(Intent intent) {
    load();
  }

  @Override
  public boolean onUnbind(Intent intent) {
    Log.d(getClass().getSimpleName(), "Service -> unbind/stop");

    write();

    return true;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(getClass().getSimpleName(), "Service -> start");
    load();
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    Log.d(getClass().getSimpleName(), "Service -> destroy");
    write();
  }

  private void load() {
    SharedPreferences preferences = getSharedPreferences(getClass().getSimpleName(), MODE_PRIVATE);
    String json = preferences.getString("json", "");
    String hash = preferences.getString("hash", "");

    if (json.equals("") && hash.equals(""))
      return;


    try {
      String computedHash = Utils.getHashedString(json);

      if (!computedHash.equals(hash)) {
        tries = TRIES_FOR_SMALL_BLOCK;
        currentMaxLockTime = FINAL_BLOCK_TIME;
        currentLockTime = currentMaxLockTime;
        return;
      }

      StringReader reader = new StringReader(json);
      JsonReader jsonReader = new JsonReader(reader);

      jsonReader.beginObject();
      while (jsonReader.hasNext()) {
        String name = jsonReader.nextName();
        if (name.equals("tries")) {
          tries = jsonReader.nextInt();
        } else if (name.equals("time")) {
          currentLockTime = jsonReader.nextInt();
        } else if (name.equals("maxTime")) {
          currentMaxLockTime = jsonReader.nextInt();
        }
      }
      jsonReader.endObject();
      reader.close();
      jsonReader.close();
    } catch (NoSuchAlgorithmException | IOException e) {
      Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
    } finally {
      preferences.edit().clear().apply();

      Thread thread = new Thread(blockRunnable);
      thread.start();
    }
  }


  private void write() {
    StringWriter writer = new StringWriter();
    JsonWriter jsonWriter = new JsonWriter(writer);

    try {
      jsonWriter.beginObject();
      jsonWriter.name("tries").value(tries);
      jsonWriter.name("time").value(currentLockTime);
      jsonWriter.name("maxTime").value(currentMaxLockTime);
      jsonWriter.endObject();

      String json = writer.toString();
      writer.flush();
      writer.close();
      jsonWriter.flush();
      jsonWriter.close();

      String hash = Utils.getHashedString(json);

      SharedPreferences preferences = getSharedPreferences(getClass().getSimpleName(), MODE_PRIVATE);
      SharedPreferences.Editor editor = preferences.edit();

      editor.putString("json", json)
          .putString("hash", hash)
          .apply();


    } catch (IOException | NoSuchAlgorithmException e) {
      Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
    }
  }

}
