package core.data;

import android.util.JsonReader;
import android.util.JsonWriter;
import core.Utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class User {

    private List<Integer> passwordIds;
    private int id;
    private String name;
    String plainPassword;
    private String passwordHash;
    private String salt;
    boolean safeLogin;

    User(int id, String name, String plainPassword, String salt, String passwordHash) {
        this.id = id;
        this.name = name;
        this.plainPassword = plainPassword;
        this.salt = salt;
        this.passwordHash = passwordHash;
        this.passwordIds = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getId() {
        return id;
    }

    public void isSafeLogin(boolean safeLogin) {
        this.safeLogin = safeLogin;
    }

    public boolean isSafeLogin() {
        return safeLogin;
    }

    public List<Integer> getPasswordIds() {
        return passwordIds;
    }

    public String getPasswordsAsJson() throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);

        jsonWriter.beginObject();

        jsonWriter.beginArray();
        for (Integer i : passwordIds) {
            jsonWriter.beginObject();
            jsonWriter.name("id");
            jsonWriter.value(i);
            jsonWriter.endObject();
        }
        jsonWriter.endArray();

        jsonWriter.name("salt");
        jsonWriter.value(Utils.getSalt());

        jsonWriter.endObject();

        String data = writer.toString();
        writer.close();
        jsonWriter.close();
        return data;
    }

    public void setPasswordIdsFromJson(String data) throws IOException {
        StringReader reader = new StringReader(data);
        JsonReader jsonReader = new JsonReader(reader);

        jsonReader.beginObject();

        while (jsonReader.hasNext()) {

            jsonReader.beginArray();

            while (jsonReader.hasNext()) {
                jsonReader.beginObject();
                String idName = jsonReader.nextName();
                if (!idName.equals("id")) continue;

                Integer id = jsonReader.nextInt();

                if (!passwordIds.contains(id)) {
                    passwordIds.add(id);
                }

                jsonReader.endObject();
            }

            jsonReader.endArray();

        }

        jsonReader.endObject();

        reader.close();
        jsonReader.close();
    }

    public void addPasswordById(int id) {
        passwordIds.add(id);
    }

    public void logout() {
        id = -1;
        name = null;
        plainPassword = null;
        salt = null;
        passwordHash = null;
        safeLogin = false;
        passwordIds.clear();
        passwordIds = null;
    }
}
