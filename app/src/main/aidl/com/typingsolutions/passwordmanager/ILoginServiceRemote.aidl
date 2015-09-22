// ILoginServiceRemote.aidl
package com.typingsolutions.passwordmanager;

import core.IServiceCallback;

interface ILoginServiceRemote {
    boolean login(in int id, in String passwordHash, in String dbPasswordHash);

    oneway void getBlockedTimeAsync(int id);
    boolean isUserBlocked(int id);
    void registerCallback(IServiceCallback callback);
    void unregisterCallback(IServiceCallback callback);
}
