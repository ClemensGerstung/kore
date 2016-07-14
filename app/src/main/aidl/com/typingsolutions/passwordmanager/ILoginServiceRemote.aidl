// ILoginServiceRemote.aidl
package com.typingsolutions.passwordmanager;

import com.typingsolutions.passwordmanager.IServiceCallback;

interface ILoginServiceRemote {
    void increaseTries();
    void resetTries();

    int getRemainingTries();
    boolean isBlocked();

    void stop();

    void registerCallback(IServiceCallback callback);
    void unregisterCallback(IServiceCallback callback);
}
