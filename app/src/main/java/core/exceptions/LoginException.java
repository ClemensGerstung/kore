package core.exceptions;

/**
 * Created by Clemens on 9/21/2015.
 */
public class LoginException extends Exception {
    public LoginException() {
    }

    public LoginException(String detailMessage) {
        super(detailMessage);
    }

    public LoginException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public LoginException(Throwable throwable) {
        super(throwable);
    }
}
