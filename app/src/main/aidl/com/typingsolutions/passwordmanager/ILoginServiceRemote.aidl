// ILoginServiceRemote.aidl
package com.typingsolutions.passwordmanager;

import core.IServiceCallback;

interface ILoginServiceRemote {
    boolean login(in int id, in String passwordHash, in String dbPasswordHash);

    oneway void getBlockedTimeAsync();
    void registerCallback(IServiceCallback callback);
    void unregisterCallback(IServiceCallback callback);
}
