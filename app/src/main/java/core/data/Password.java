package core.data;

import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.JsonWriter;
import core.Utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

public class Password {
    private int id;
    private int position;
    private String username;
    private String program;
    private NavigableMap<Integer, PasswordHistory> passwordHistory;


    Password(int id, int position, String username, String program) {
        this.id = id;
        this.position = position;
        this.username = username;
        this.program = program;
        this.passwordHistory = new TreeMap<>();
    }

    public Password() {
        this(Integer.MIN_VALUE, Integer.MIN_VALUE, null, null);
    }

    public void logout() {
        id = Integer.MIN_VALUE;
        position = Integer.MIN_VALUE;
        username = null;
        program = null;
        passwordHistory.clear();
        passwordHistory = null;
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

    public Set<Integer> getPasswordIds() {
        return passwordHistory.keySet();
    }

    public List<PasswordHistory> getPasswordHistory() {
        return new ArrayList<>(passwordHistory.values());
    }

    public void setPasswordHistoryItem(Integer id, PasswordHistory item) {
        passwordHistory.put(id, item);
    }

    public String getFirstItem() {
        return passwordHistory.firstEntry().getValue().getValue();
    }

    public PasswordHistory getFirstHistoryItem() {
        return passwordHistory.firstEntry().getValue();
    }

    public void addPasswordHistoryItem(Integer id, PasswordHistory item) {
        TreeMap<Integer, PasswordHistory> tmp = new TreeMap<>(passwordHistory);
        passwordHistory.clear();
        passwordHistory.put(id, item);
        passwordHistory.putAll(tmp);
        tmp.clear();
    }

    public Integer getKeyAt(int position) {
        Iterator<Integer> iterator = passwordHistory.keySet().iterator();
        Integer integer = null;
        for (int i = 0; i < position; i++) {
            if (iterator.hasNext()) {
                integer = iterator.next();
            } else {
                return null;
            }
        }
        return integer;
    }

    public boolean hasId() {
        return id != -1;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setUsername(@Nullable String username) {
        if(username == null) return;
        this.username = username;
    }

    public void setProgram(@Nullable String program) {
        if(program == null) return;
        this.program = program;
    }

    @Override
    public String toString() {
        return "Password{" +
                "username='" + username + '\'' +
                "password='" + getFirstItem() + '\'' +
                ", program='" + program + '\'' +
                '}';
    }

    public String getJson() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        jsonWriter.beginObject();
        jsonWriter.name("username").value(username);
        jsonWriter.name("program").value(program);
        jsonWriter.name("position").value(position);
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
        while (jsonReader.hasNext()) {
            String jsonName = jsonReader.nextName();
            if (jsonName.equals("username")) {
                username = jsonReader.nextString();
            } else if (jsonName.equals("program")) {
                program = jsonReader.nextString();
            } else if (jsonName.equals("history")) {
                jsonReader.beginArray();
                while (jsonReader.hasNext()) {
                    jsonReader.beginObject();
                    String idName = jsonReader.nextName();
                    if (!idName.equals("id")) continue;
                    passwordHistory.keySet().add(jsonReader.nextInt());
                    jsonReader.endObject();
                }
                jsonReader.endArray();
            } else if(jsonName.equals("position")) {
                position = jsonReader.nextInt();
            }
        }
        jsonReader.close();

        reader.close();
        jsonReader.close();
    }

    public static Password getFromJson(int id, String data) throws IOException {
        Password password = new Password();
        password.id = id;
        password.setFromJson(data);
        return password;
    }

    public static Password createSimplePassword(String program, String username) {
        Password password = new Password();
        password.username = username;
        password.program = program;
        password.position = Integer.MIN_VALUE;
        return password;
    }
}
