package core.data;

import android.util.JsonReader;
import android.util.JsonWriter;
import core.Utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Password {
    private int id;
    private int position;
    private String username;
    private String program;
    private HashMap<Integer, PasswordHistory> passwordHistory;


    Password(int id, int position, String username, String program) {
        this.id = id;
        this.position = position;
        this.username = username;
        this.program = program;
        this.passwordHistory = new HashMap<>();
    }

    public Password() {
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        PasswordHistory history = new PasswordHistory(id, value, convertedDate);
//        passwordHistory.add(history);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Password)) return false;

        Password password = (Password) o;

        return id == password.id && position == password.position && username.equals(password.username) && program.equals(password.program);
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

    public HashMap<Integer, PasswordHistory> getPasswordHistory() {
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

    public String getJson() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        jsonWriter.beginObject();
        jsonWriter.name("username").value(username);
        jsonWriter.name("program").value(program);
        jsonWriter.name("history");
        jsonWriter.beginArray();
        for (Integer i : passwordHistory.keySet()) {
            jsonWriter.beginObject();
            jsonWriter.name("id").value(i);
            jsonWriter.endObject();
        }
        jsonWriter.endArray();

        jsonWriter.name("salt").value(Utils.getSalt());

        jsonWriter.endObject();

        String result = writer.toString();
        writer.close();
        jsonWriter.close();
        return result;
    }

    void setFromJson(String data) throws IOException {
        StringReader reader = new StringReader(data);
        JsonReader jsonReader = new JsonReader(reader);

        jsonReader.beginObject();
        while(jsonReader.hasNext()) {
            String jsonName = jsonReader.nextName();
            if(jsonName.equals("username")) {
                username = jsonReader.nextString();
            } else if(jsonName.equals("program")) {
                program = jsonReader.nextString();
            } else if(jsonName.equals("history")) {
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    jsonReader.beginObject();
                    String idName = jsonReader.nextName();
                    if(!idName.equals("id")) continue;
                    passwordHistory.keySet().add(jsonReader.nextInt());
                    jsonReader.endObject();
                }
                jsonReader.endArray();
            }

        }
        jsonReader.close();

        reader.close();
        jsonReader.close();
    }

    public static Password getFromJson(String data) throws IOException {
        Password password = new Password();
        password.setFromJson(data);
        return password;
    }
}
