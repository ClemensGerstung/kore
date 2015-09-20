package core.exceptions;

public class UserProviderException extends Exception {
    public UserProviderException() {
        super();
    }

    public UserProviderException(String detailMessage) {
        super(detailMessage);
    }

    public UserProviderException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public UserProviderException(Throwable throwable) {
        super(throwable);
    }
}
