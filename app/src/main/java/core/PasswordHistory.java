package core;

import java.util.Date;

public class PasswordHistory {
    private int id;
    private String value;

    private Date changedDate;

    public PasswordHistory(int id, String value, Date changedDate) {
        this.id = id;
        this.value = value;
        this.changedDate = changedDate;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public Date getChangedDate() {
        return changedDate;
    }
}