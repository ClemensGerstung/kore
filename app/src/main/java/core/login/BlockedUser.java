package core.login;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import com.typingsolutions.passwordmanager.handler.LoginBlockHandler;
import com.typingsolutions.passwordmanager.services.LoginService;

public class BlockedUser {
    private BlockedUserList blockedUsers;
    int id = -1;
    int timeRemaining = 0;
    int completeTime = 0;
    int tries = 0;

    private HandlerThread handlerThread;
    private Handler handler;
    private Looper looper;

    public BlockedUser(BlockedUserList blockedUsers) {
        this.blockedUsers = blockedUsers;
        handlerThread = new HandlerThread(Integer.toHexString(id), HandlerThread.MAX_PRIORITY);
        handlerThread.start();
        looper = handlerThread.getLooper();

        handler = new LoginBlockHandler(blockedUsers.loginService, this, looper);
    }

    public boolean isBlocked() {
        return timeRemaining > 0;
    }

    public void increaseTries() {
        boolean start = false;
        tries = tries + 1;
        if (tries == LoginService.TRIES_FOR_SMALL_BLOCK) {
            completeTime = LoginService.SMALL_BLOCK_TIME;
            timeRemaining = completeTime;
            start = true;
        } else if (tries == LoginService.TRIES_FOR_MEDIUM_BLOCK) {
            completeTime = LoginService.MEDIUM_BLOCK_TIME;
            timeRemaining = completeTime;
            start = true;
        } else if (tries == LoginService.TRIES_FOR_LARGE_BLOCK) {
            completeTime = LoginService.LARGE_BLOCK_TIME;
            timeRemaining = completeTime;
            start = true;
        } else if (tries >= LoginService.TRIES_FOR_FINAL_BLOCK) {
            completeTime = LoginService.FINAL_BLOCK_TIME;
            timeRemaining = completeTime;
            start = true;
        }
        if (start) {
            handler.sendEmptyMessage(0);
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


    @Override
    public String toString() {
        return "BlockedUser{" +
                "id=" + id +
                ", timeRemaining=" + timeRemaining +
                ", tries=" + tries +
                '}';
    }

    public int getId() {
        return id;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public int getCompleteTime() {
        return completeTime;
    }

    public void reduceTimeRemaining(int value) {
        timeRemaining = timeRemaining - value;
    }
}
