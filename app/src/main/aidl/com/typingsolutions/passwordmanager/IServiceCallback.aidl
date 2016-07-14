package com.typingsolutions.passwordmanager;

interface IServiceCallback {
    void getLockTime(int time, int completeTime);
    void onStart();
    void onFinish();
}
