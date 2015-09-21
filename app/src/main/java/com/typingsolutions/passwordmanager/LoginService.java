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

    private final RemoteCallbackList<IServiceCallback> callbacks = new RemoteCallbackList<>();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final ILoginServiceRemote.Stub binder  = new ILoginServiceRemote.Stub() {

        @Override
        public boolean login(String username, String passwordHash, String dbPasswordHash) throws RemoteException {
            return false;
        }

        @Override
        public void getBlockedTimeAsync() throws RemoteException {
            final int size = callbacks.beginBroadcast();

            for (int i = 0; i < size; i++) {
                try {
                    callbacks.getBroadcastItem(i).getLockTime(null, 0, 0);
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
}
