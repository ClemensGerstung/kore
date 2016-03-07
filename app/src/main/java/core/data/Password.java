package core.data;

import android.database.Cursor;
import android.support.annotation.Nullable;
import core.Dictionary;

import java.util.Collection;
import java.util.Comparator;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Password)) return false;

    Password password = (Password) o;

    if (id != password.id) return false;
    if (position != password.position) return false;
    return username.equals(password.username) && program.equals(password.program);
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

  public String getFirstItem() {
    return getFirstHistoryItem().getValue();
  }

  public PasswordHistory getFirstHistoryItem() {
    return passwordHistory.getFirstIterator().getValue();
  }

  public void addPasswordHistoryItem(Integer id, PasswordHistory item) {
    passwordHistory.addFirst(id, item);
  }

  public Dictionary<Integer, PasswordHistory> getPasswordHistory() {
    return passwordHistory;
  }

  public Collection<Integer> getPasswordIds() {
    return passwordHistory.keys();
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

  public void swapPositionWith(Password password) {
    int tmp = position;
    position = password.getPosition();
    password.setPosition(tmp);
  }

  void orderHistoryByDate() {
    passwordHistory.sortByValue(new Comparator<PasswordHistory>() {
      @Override
      public int compare(PasswordHistory lhs, PasswordHistory rhs) {
        return rhs.getChangedDate().compareTo(lhs.getChangedDate());
      }
    });
  }

  public static Password getFromCursor(Cursor cursor) {
    int id = cursor.getInt(0);
    String username = cursor.getString(1);
    String program = cursor.getString(2);
    int position = cursor.getInt(3);
    int historyId = cursor.getInt(4);

    Password password = new Password(id, position, username, program);

    PasswordHistory history = PasswordHistory.getFromCursor(cursor);
    password.addPasswordHistoryItem(historyId, history);

    return password;
  }

  public void merge(Password nextPassword) {
    passwordHistory.addAll(nextPassword.passwordHistory,
        Dictionary.InsertOption.After,
        Dictionary.IterationOption.Forwards);
  }
}
