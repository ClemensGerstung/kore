// ILoginServiceRemote.aidl
package com.typingsolutions.passwordmanager;

import core.IServiceCallback;

interface ILoginServiceRemote {
    void increaseTries();

    oneway void getBlockedTimeAsync();
    boolean isUserBlocked();
    int getMaxBlockTime();

    void registerCallback(IServiceCallback callback);
    void unregisterCallback(IServiceCallback callback);
}
