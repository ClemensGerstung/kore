package core.data;

import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.JsonWriter;
import core.*;
import core.Dictionary;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

public class Password {
  private int id;
  private int position;
  private String username;
  private String program;
  private core.Dictionary<Integer, PasswordHistory> passwordHistory;


  Password(int id, int position, String username, String program) {
    this.id = id;
    this.position = position;
    this.username = username;
    this.program = program;
    this.passwordHistory = new Dictionary<>();
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

  public void setPasswordHistoryItem(Integer id, PasswordHistory item) {
    passwordHistory.setForKey(id, item, Dictionary.IterationOption.Forwards);
  }

  public String getFirstItem() {
    return getFirstHistoryItem().getValue();
  }

  public PasswordHistory getFirstHistoryItem() {
    return passwordHistory.getFirstIterator().getValue();
  }

  public void addPasswordHistoryItem(Integer id, PasswordHistory item) {
    passwordHistory.addFirst(id, item);
  }

  public Integer getKeyAt(int position) {
    return passwordHistory.getKeyAt(position);
  }

  public Collection<Integer> getPasswordIds() {
    return passwordHistory.keys();
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
    if (username == null) return;
    this.username = username;
  }

  public void setProgram(@Nullable String program) {
    if (program == null) return;
    this.program = program;
  }

  public int getHistoryCount() {
    return passwordHistory.size();
  }

  public PasswordHistory getItemAt(int index) {
    return passwordHistory.getValueAt(index);
  }

  @Override
  public String toString() {
    return "Password{" +
        "username='" + username + '\'' +
        "password='" + getFirstItem() + '\'' +
        ", program='" + program + '\'' +
        '}';
  }


  @Deprecated
  void setFromJson(String data) throws IOException {
    StringReader reader = new StringReader(data);
    JsonReader jsonReader = new JsonReader(reader);

    jsonReader.beginObject();
    while (jsonReader.hasNext()) {
      String jsonName = jsonReader.nextName();
      switch (jsonName) {
        case "username":
          username = jsonReader.nextString();
          break;
        case "program":
          program = jsonReader.nextString();
          break;
        case "history":
          jsonReader.beginArray();
          while (jsonReader.hasNext()) {
            jsonReader.beginObject();
            String idName = jsonReader.nextName();
            if (!idName.equals("id")) continue;
            passwordHistory.addLast(jsonReader.nextInt(), null);
            jsonReader.endObject();
          }
          jsonReader.endArray();
          break;
        case "position":
          position = jsonReader.nextInt();
          break;
        default:
          jsonReader.nextString();
          break;
      }
    }
    jsonReader.close();

    reader.close();
    jsonReader.close();
  }

  @Deprecated
  public static Password getFromJson(int id, String data) throws IOException {
    Password password = new Password();
    password.id = id;
    password.setFromJson(data);
    return password;
  }

  public void swapPositionWith(Password password) {
    int tmp = position;
    position = password.getPosition();
    password.setPosition(position);
  }
}
