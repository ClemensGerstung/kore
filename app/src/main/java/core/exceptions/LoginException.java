package core.exceptions;


public class LoginException extends Exception {

    public static final int BLOCKED = 1;

    public static final int WRONG = 2;

    private int state;

    public LoginException(String detailMessage, int state) {
        super(detailMessage);
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
