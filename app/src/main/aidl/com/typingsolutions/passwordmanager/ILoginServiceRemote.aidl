// ILoginServiceRemote.aidl
package com.typingsolutions.passwordmanager;

import core.IServiceCallback;

interface ILoginServiceRemote {
    void increaseTries();
    void resetTries();

    int getRemainingTries();
    boolean isBlocked();

    void registerCallback(IServiceCallback callback);
    void unregisterCallback(IServiceCallback callback);
}
