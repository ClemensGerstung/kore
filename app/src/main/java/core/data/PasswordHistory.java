package core.data;

import android.util.JsonReader;
import android.util.JsonWriter;
import core.Utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

public class PasswordHistory {
    private String value;
    private Date changedDate;

    PasswordHistory(String value, Date changedDate) {
        this.value = value;
        this.changedDate = changedDate;
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

        if (value != null ? !value.equals(history.value) : history.value != null) return false;
        return !(changedDate != null ? !changedDate.equals(history.changedDate) : history.changedDate != null);

    }

    @Override
    public int hashCode() {
        int result = 31;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (changedDate != null ? changedDate.hashCode() : 0);
        return result;
    }

    public String getJson() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        jsonWriter.beginObject();
        jsonWriter.name("value").value(value);
        jsonWriter.name("dateChanged").value(Utils.getDateAsSimpleString(changedDate));
        jsonWriter.name("salt").value(Utils.getSalt());
        jsonWriter.endObject();

        String result = writer.toString();
        writer.close();
        jsonWriter.close();
        return result;
    }

    public void setFromJson(String data) throws Exception {
        StringReader reader = new StringReader(data);
        JsonReader jsonReader = new JsonReader(reader);

        jsonReader.beginObject();
        while(jsonReader.hasNext()) {
            String jsonName = jsonReader.nextName();
            if(jsonName.equals("value")) {
                value = jsonReader.nextString();
            } else if (jsonName.equals("dateChanged")) {
                changedDate = Utils.getDateFromString(jsonReader.nextString());
            }
        }
        jsonReader.endObject();

        reader.close();
        jsonReader.close();
    }
}