package core;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

public class PasswordHistory {
    private int id;
    private String value;
    private Date changedDate;

    PasswordHistory(int id, String value, Date changedDate) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordHistory)) return false;

        PasswordHistory history = (PasswordHistory) o;

        if (id != history.id) return false;
        if (value != null ? !value.equals(history.value) : history.value != null) return false;
        return !(changedDate != null ? !changedDate.equals(history.changedDate) : history.changedDate != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (changedDate != null ? changedDate.hashCode() : 0);
        return result;
    }

    public String getJson() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        jsonWriter.beginObject();
        jsonWriter.name("value");
        jsonWriter.value(value);
        jsonWriter.name("dateChanged");
        jsonWriter.value(Utils.getDateAsSimpleString(changedDate));
        jsonWriter.endObject();

        String result = writer.toString();
        writer.close();
        jsonWriter.close();
        return result;
    }
}