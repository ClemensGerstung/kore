package core;

import android.text.style.TtsSpan;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    public int addToHistory(String password) {
        String date = Utils.getDate();

        return 0;
    }

    void addHistoryItem(int id, String value, String date) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
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
}
