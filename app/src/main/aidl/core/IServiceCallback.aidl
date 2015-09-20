package core;

import core.User;

interface IServiceCallback {
    void getLockTime(in User user, int time, int maxTime);
}
