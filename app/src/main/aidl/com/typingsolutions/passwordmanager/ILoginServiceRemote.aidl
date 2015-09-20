// ILoginServiceRemote.aidl
package com.typingsolutions.passwordmanager;

import core.User;
import core.IServiceCallback;

interface ILoginServiceRemote {
    User login(in String username, in String passwordHash);

    void getBlockedTimeAsync();
    void registerCallback(IServiceCallback callback);
    void unregisterCallback(IServiceCallback callback);
}
