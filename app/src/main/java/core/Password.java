package core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Password {
    private int id;
    private String username;
    private String program;
    private List<String> passwordHistory;

    public Password(int id, String username, String program) {
        this.id = id;
        this.username = username;
        this.program = program;
        this.passwordHistory = new ArrayList<String>();
    }

    public void logout() {
        id = Integer.MIN_VALUE;
        username = null;
        program = null;
        passwordHistory.clear();
        passwordHistory = null;
    }

    public int addToHistory(String password) {
        String date = getDate();


        return 0;
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
