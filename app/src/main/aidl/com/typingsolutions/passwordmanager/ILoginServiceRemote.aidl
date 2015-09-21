// ILoginServiceRemote.aidl
package com.typingsolutions.passwordmanager;

import core.User;
import core.IServiceCallback;

interface ILoginServiceRemote {
    boolean login(in String username, in String passwordHash, in String dbPasswordHash);

    oneway void getBlockedTimeAsync();
    void registerCallback(IServiceCallback callback);
    void unregisterCallback(IServiceCallback callback);
}
