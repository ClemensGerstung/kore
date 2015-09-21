package com.typingsolutions.passwordmanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import core.IServiceCallback;
import core.User;


public class LoginService extends Service {

    // tries until block
    static int TRIES_FOR_SMALL_BLOCK = 3;
    static int TRIES_FOR_MEDIUM_BLOCK = 6;
    static int TRIES_FOR_LARGE_BLOCK = 9;
    static int TRIES_FOR_FINAL_BLOCK = 12;

    // block times in ms
    static int SMALL_BLOCK_TIME = 30000;    // 0.5 minutes
    static int MEDIUM_BLOCK_TIME = 60000;   // 1 minute
    static int LARGE_BLOCK_TIME = 150000;   // 2.5 minutes
    static int FINAL_BLOCK_TIME = 300000;   // 5 minutes

    private final RemoteCallbackList<IServiceCallback> callbacks = new RemoteCallbackList<>();



    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final ILoginServiceRemote.Stub binder  = new ILoginServiceRemote.Stub() {

        @Override
        public boolean login(int id, String passwordHash, String dbPasswordHash) throws RemoteException {
            if(!passwordHash.equals(dbPasswordHash)) {
                // TODO: add to a list with blocked users
            }
            // TODO: remove form list with blocked users

            return true;
        }

        @Override
        public void getBlockedTimeAsync() throws RemoteException {
            final int size = callbacks.beginBroadcast();

            for (int i = 0; i < size; i++) {
                try {
                    callbacks.getBroadcastItem(i).getLockTime(0, 0, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            callbacks.finishBroadcast();
        }

        @Override
        public void registerCallback(IServiceCallback callback) throws RemoteException {
            if(callback != null) {
                callbacks.register(callback);
            }
        }

        @Override
        public void unregisterCallback(IServiceCallback callback) throws RemoteException {
            if(callback != null) {
                callbacks.unregister(callback);
            }
        }
    };

    private class BlockedUserList {
        class BlockedUser {
            int id;
            int timeRemaining;
            int completeTime;
            int tries;


        }


        public void add(int id) {

        }

        public void remove(int id) {

        }
    }
}
