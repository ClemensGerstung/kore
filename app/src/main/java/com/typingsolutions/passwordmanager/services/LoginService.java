package com.typingsolutions.passwordmanager.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.util.Log;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import core.IServiceCallback;
import core.Utils;
import core.login.BlockedUser;
import core.login.BlockedUserList;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public class LoginService extends Service {

    public static final String INTENT_ACTION = "com.typingsolutions.passwordmanager.service.LoginService.UPDATE_BLOCKING";
    public static final String INTENT_BLOCK = "com.typingsolutions.passwordmanager.service.LoginService.BLOCK";

    public static final int SLEEP_TIME = 1000;

    // tries until block
    public static int TRIES_FOR_SMALL_BLOCK = 3;
    public static int TRIES_FOR_MEDIUM_BLOCK = 6;
    public static int TRIES_FOR_LARGE_BLOCK = 9;
    public static int TRIES_FOR_FINAL_BLOCK = 12;

    // block times in ms
    public static int SMALL_BLOCK_TIME = 30000;    // 0.5 minutes
    public static int MEDIUM_BLOCK_TIME = 60000;   // 1 minute
    public static int LARGE_BLOCK_TIME = 150000;   // 2.5 minutes
    public static int FINAL_BLOCK_TIME = 300000;   // 5 minutes

    private final RemoteCallbackList<IServiceCallback> callbacks = new RemoteCallbackList<>();
    private final BlockedUserList blockedUserList = new BlockedUserList(this);

    private final ILoginServiceRemote.Stub binder = new ILoginServiceRemote.Stub() {

        @Override
        public boolean login(int id, String passwordHash, String dbPasswordHash) throws RemoteException {

            if (!passwordHash.equals(dbPasswordHash)) {
                blockedUserList.add(id);
                return false;
            }
            blockedUserList.remove(id);

            return true;
        }

        @Override
        public void getBlockedTimeAsync(int id) throws RemoteException {
            final int size = callbacks.beginBroadcast();

            BlockedUser user = blockedUserList.getUserById(id);
            if (user == null){
                callbacks.finishBroadcast();
                return;
            }

//            Log.d(getClass().getSimpleName(), String.format("Get block time async for %s callbacks", size));
            for (int i = 0; i < size; i++) {
                callbacks.getBroadcastItem(i).getLockTime(user.getTimeRemaining(), user.getCompleteTime());
            }

            callbacks.finishBroadcast();
        }

        @Override
        @Deprecated
        public boolean isUserBlocked(int id) throws RemoteException {
            BlockedUser user = blockedUserList.getUserById(id);
            return user != null && user.isBlocked();
        }

        @Override
        @Deprecated
        public int getMaxBlockTime(int id) throws RemoteException {
            BlockedUser user = blockedUserList.getUserById(id);
            if (user == null) return -1;
            return user.getCompleteTime();
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

        try {
            String json = blockedUserList.toJson();
            String hash = Utils.getHashedString(json);

//            Log.d(getClass().getSimpleName(), json);

            editor.putString("json", json);
            editor.putString("hash", hash);

            editor.apply();
        } catch (IOException | NoSuchAlgorithmException e) {
            Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
        }

        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        readSerializedData();
        return START_STICKY;
    }

    private void readSerializedData() {
        SharedPreferences preferences = getSharedPreferences(getClass().getSimpleName(), MODE_PRIVATE);
        String json = preferences.getString("json", "");
        String hash = preferences.getString("hash", "");

        try {
            String computedHash = Utils.getHashedString(json);
            blockedUserList.fromJson(json, hash.equals(computedHash));
        } catch (NoSuchAlgorithmException | IOException e) {
            Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
        } finally {
            preferences.edit().clear().apply();
        }
    }
}
