package core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Password {
    private int id;
    private int position;
    private String username;
    private String program;
    private List<PasswordHistory> passwordHistory;


    public Password(int id, int position, String username, String program) {
        this.id = id;
        this.position = position;
        this.username = username;
        this.program = program;
        this.passwordHistory = new ArrayList<>();
    }

    public void logout() {
        id = Integer.MIN_VALUE;
        position = Integer.MIN_VALUE;
        username = null;
        program = null;
        passwordHistory.clear();
        passwordHistory = null;
    }

    void addHistoryItem(int id, String value, String date) {
        DateFormat dateFormat = new SimpleDateFormat();
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        PasswordHistory history = new PasswordHistory(id, value, convertedDate);
        passwordHistory.add(history);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Password)) return false;

        Password password = (Password) o;

        if (id != password.id) return false;
        if (position != password.position) return false;
        if (!username.equals(password.username)) return false;
        return program.equals(password.program);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + position;
        result = 31 * result + username.hashCode();
        result = 31 * result + program.hashCode();
        return result;
    }

    public int getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public String getUsername() {
        return username;
    }

    public String getProgram() {
        return program;
    }

    public List<PasswordHistory> getPasswordHistory() {
        return passwordHistory;
    }

    public PasswordHistory getFirstItem() {
        return passwordHistory.get(0);
    }

    @Override
    public String toString() {
        return "Password{" +
                "username='" + username + '\'' +
                "password='" + getFirstItem().getValue() + '\'' +
                ", program='" + program + '\'' +
                '}';
    }
}
