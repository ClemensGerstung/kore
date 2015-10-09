package com.typingsolutions.passwordmanager.services;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import core.IServiceCallback;
import core.login.BlockedUser;
import core.login.BlockedUserList;


public class LoginService extends Service {


    public static final String INTENT_ACTION = "com.typingsolutions.passwordmanager.service.LoginService.UPDATE_BLOCKING";

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

            for (int i = 0; i < size; i++) {
                BlockedUser user = blockedUserList.getUserById(id);
                if (user == null) continue;
//                if (!user.isBlocked()) continue;
                callbacks.getBroadcastItem(i).getLockTime(user.getTimeRemaining(), user.getCompleteTime());
            }

            callbacks.finishBroadcast();
        }

        @Override
        public boolean isUserBlocked(int id) throws RemoteException {
            BlockedUser user = blockedUserList.getUserById(id);
            return user != null && user.isBlocked();
        }

        @Override
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
        Log.d(getClass().getSimpleName(), "onBind");
        int i = 0;
        for (BlockedUser user : blockedUserList) {
            Log.d(getClass().getSimpleName(), user.toString());
            i++;
        }
        Log.d(getClass().getSimpleName(), Integer.toString(i));

        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);

        Log.d(getClass().getSimpleName(), "onRebind");
        int i = 0;
        for (BlockedUser user : blockedUserList) {
            Log.d(getClass().getSimpleName(), user.toString());
            i++;
        }
        Log.d(getClass().getSimpleName(), Integer.toString(i));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
