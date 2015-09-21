package com.typingsolutions.passwordmanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import core.IServiceCallback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class LoginService extends Service {

    public static final String INTENT_ACTION = "com.typingsolutions.passwordmanager.service.LoginService.UPDATE_BLOCKING";

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

    private final BlockedUserList blockedUserList = new BlockedUserList();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

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
        public void getBlockedTimeAsync() throws RemoteException {
            final int size = callbacks.beginBroadcast();

            for (int i = 0; i < size; i++) {
                for (BlockedUserList.BlockedUser user : blockedUserList) {
                    callbacks.getBroadcastItem(i).getLockTime(user.id, user.timeRemaining, user.completeTime);
                }
            }

            callbacks.finishBroadcast();
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

    private class BlockedUserList implements Iterable<BlockedUserList.BlockedUser> {
        @Override
        public Iterator<BlockedUser> iterator() {
            return blockedUserList.iterator();
        }

        class BlockedUser {
            int id = -1;
            int timeRemaining = 0;
            int completeTime = 0;
            int tries = 0;

            private Thread lock = new Thread(new Runnable() {
                @Override
                public void run() {
                    long lastSystemTime = SystemClock.currentThreadTimeMillis();

                    while(timeRemaining >= 0){
                        long currentSystemTime = SystemClock.currentThreadTimeMillis();
                        int subtract = (int) (currentSystemTime - lastSystemTime);
                        lastSystemTime = currentSystemTime;
                        timeRemaining = timeRemaining - subtract;

                        Intent intent = new Intent(INTENT_ACTION);
                        getApplicationContext().sendBroadcast(intent);

                        SystemClock.sleep(1000);
                    }
                }
            });

            void increaseTries() {
                boolean start = false;
                tries = tries + 1;
                if (tries == TRIES_FOR_SMALL_BLOCK) {
                    completeTime = SMALL_BLOCK_TIME;
                    timeRemaining = completeTime;
                    start = true;
                } else if (tries == TRIES_FOR_MEDIUM_BLOCK) {
                    completeTime = MEDIUM_BLOCK_TIME;
                    timeRemaining = completeTime;
                    start = true;
                } else if (tries == TRIES_FOR_LARGE_BLOCK) {
                    completeTime = LARGE_BLOCK_TIME;
                    timeRemaining = completeTime;
                    start = true;
                } else if (tries >= TRIES_FOR_FINAL_BLOCK) {
                    completeTime = FINAL_BLOCK_TIME;
                    timeRemaining = completeTime;
                    start = true;
                }
                if (start) {
                    lock.start();
                }
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                BlockedUser that = (BlockedUser) o;

                return id == that.id;

            }

            @Override
            public int hashCode() {
                int result = id;
                result = 31 * result + timeRemaining;
                result = 31 * result + completeTime;
                result = 31 * result + tries;
                return result;
            }
        }

        private List<BlockedUser> blockedUserList;

        public BlockedUserList() {
            blockedUserList = new ArrayList<>();
        }

        public void add(int id) {
            BlockedUser user = null;

            for (BlockedUser blockedUser : blockedUserList) {
                if (blockedUser.id == id) {
                    user = blockedUser;
                    break;
                }
            }

            if (user != null) {
                user.increaseTries();
            } else {
                user = new BlockedUser();
                user.id = id;
                user.increaseTries();
                blockedUserList.add(user);
            }
        }

        public void remove(int id) {
            for (BlockedUser blockedUser : blockedUserList) {
                if (blockedUser.id == id) {
                    blockedUserList.remove(blockedUser);
                    break;
                }
            }
        }
    }
}
