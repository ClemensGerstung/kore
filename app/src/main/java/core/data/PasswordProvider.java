package core.data;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import core.DatabaseProvider;
import core.Dictionary;
import core.Utils;
import core.async.AsyncDatabasePipeline;
import core.async.SqlDeleteTask;
import core.async.SqlInsertTask;
import core.async.SqlUpdateTask;
import core.callback.AddHistoryCallback;
import core.callback.AddNewHistoryCallback;
import core.callback.AddPasswordCallback;
import net.sqlcipher.Cursor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Deprecated
public class PasswordProvider {
  private static PasswordProvider Instance;

  private Context context;
  private List<Password> passwords;
  private PasswordActionListener passwordActionListener;
  private boolean safe;

  private PasswordProvider(Context context) {
    this.context = context;
    this.passwords = new ArrayList<>();
  }

  public static PasswordProvider getInstance(Context context) {
    if (Instance == null)
      Instance = new PasswordProvider(context);
    return Instance;
  }

  public int size() {
    return passwords.size();
  }

  public Password get(int index) {
    return passwords.get(index);
  }

  public int merge(List<Password> passwords) {
    int merged = 0;

    if (passwords.size() == 0)
      return merged;

    // iterate through all items in the list to merge
    for (final Password password : passwords) {

      // is the password already existing?
      boolean exists = false;

      // iterate through all existing passwords
      for (final Password existing : this.passwords) {

        // equals the current iterator of the existing items the current iterator of the merging passwords?
        if (password.getUsername().equals(existing.getUsername()) && password.getProgram().equals(existing.getProgram())) {

          // yes - iterate through all history items of the merging password and add them to the existing
          for (final PasswordHistory history : password.getPasswordHistory().values()) {

            // current existing password already contains the merging history item
            if (existing.getPasswordHistory().containsValue(history, Dictionary.IterationOption.Forwards))
              continue;

            AddHistoryCallback historyCallback = new AddHistoryCallback(context, history.getValue(), existing, history.getChangedDate());

            ContentValues values = new ContentValues(3);
            values.put("password", history.getValue());
            values.put("changed", Utils.getStringFromDate(history.getChangedDate()));
            values.put("passwordId", existing.getId());

            SqlInsertTask insertTask = new SqlInsertTask(DatabaseProvider.getConnection(context).getDatabase(), "history", "", values, historyCallback);
            insertTask.execute();
          }

          // password exists and doesn't need to be added separately
          exists = true;
          merged++;
          break;
        }
      }

      // merging password doesn't exist in the existing list
      if (!exists) {
        final int position = this.passwords.size() + 1;



        merged++;
      }
    }

    return merged;
  }

  public Password getById(int id) {
    for (Password password : passwords) {
      if (password.getId() == id) {
        return password;
      }
    }
    return null;
  }

  public static void logoutComplete() {
    Instance.simpleLogout();
    Instance.context = null;
    Instance = null;
  }

  public static boolean isLoggedIn() {
    return Instance != null;
  }

  void simpleLogout() {
    for (Password p : passwords) {
      p.logout();
    }
    passwords.clear();
  }

  public void order(int which) {
    Comparator<Password> comparator = null;

    if (which == 0) {           // order by username ascending
      comparator = new PasswordComparator("getUsername", false);
    } else if (which == 1) {    // order by username descending
      comparator = new PasswordComparator("getUsername", true);
    } else if (which == 2) {    // order by password ascending
      comparator = new PasswordComparator("getFirstItem", false);
    } else if (which == 3) {    // order by password descending
      comparator = new PasswordComparator("getFirstItem", true);
    } else if (which == 4) {    // order by program ascending
      comparator = new PasswordComparator("getProgram", false);
    } else if (which == 5) {    // order by program descending
      comparator = new PasswordComparator("getProgram", true);
    }

    if (comparator != null) {
      Collections.sort(passwords, comparator);

      if (passwordActionListener != null)
        passwordActionListener.onOrder();
    }
  }

  public void addPassword(String program, String username, String password) throws Exception {
    int position = passwords.size() + 1;

    DatabaseProvider provider = DatabaseProvider.getConnection(context);
    AddPasswordCallback callback = new AddPasswordCallback(context, username, program, password, position);

    ContentValues values = new ContentValues(3);
    values.put("username", username);
    values.put("program", program);
    values.put("position", position);

    SqlInsertTask insert = new SqlInsertTask(provider.getDatabase(), "passwords", "", values, callback);
    insert.execute();
  }

  public void addPassword(Password password) {

    password.orderHistoryByDate();

    if (!passwords.contains(password))
      passwords.add(password);

    if (passwordActionListener != null)
      passwordActionListener.onPasswordAdded(password);
  }

  public void editPassword(final int id, String newPassword) throws Exception {
    AddHistoryCallback callback = new AddHistoryCallback(context, newPassword, this.getById(id));

    ContentValues values = new ContentValues(3);
    values.put("password", newPassword);
    values.put("changed", Utils.getToday());
    values.put("passwordId", id);

    SqlInsertTask insertTask = new SqlInsertTask(DatabaseProvider.getConnection(context).getDatabase(), "history", "", values, callback);
    insertTask.execute();
  }

  public void editPassword(int id, @Nullable String program, @Nullable String username) {
    Password password = getById(id);
    password.setUsername(username);
    password.setProgram(program);

    ContentValues contentValues = new ContentValues(2);
    contentValues.put("username", username);
    contentValues.put("program", program);

    SqlUpdateTask updateTask = new SqlUpdateTask(DatabaseProvider.getConnection(context).getDatabase(), "passwords", contentValues, "id=?", new String[]{Integer.toString(id)});
    updateTask.execute();

    editPassword(password);
  }

  public void editPassword(Password password) {
    int index = passwords.indexOf(password);
    passwords.set(index, password);

    if (passwordActionListener != null)
      passwordActionListener.onPasswordEdited(password, password.getFirstHistoryItem());
  }

  public Password removePassword(int position) {
    return removePassword(get(position));
  }

  public Password removePassword(final Password password) {
    DatabaseProvider provider = DatabaseProvider.getConnection(context);

    List<Integer> list = new ArrayList<>(password.getPasswordIds());
    for (Integer i : list) {
      SqlDeleteTask deleteTask = new SqlDeleteTask(provider.getDatabase(), "history", "id=?", new String[]{i.toString()});
      deleteTask.execute();
    }

    SqlDeleteTask deleteTask = new SqlDeleteTask(provider.getDatabase(), "passwords", "id=?", new String[] {Integer.toString(password.getId())});

    passwords.remove(password);

    if (passwordActionListener != null)
      passwordActionListener.onPasswordRemoved(password);

    return password;
  }

  public void swapPassword(int from, int to) {
    Collections.swap(passwords, from, to);
    Password fromPassword = get(from);
    Password toPassword = get(to);
    fromPassword.swapPositionWith(toPassword);

    // TODO: update in database
  }

  public void setPasswordActionListener(PasswordActionListener passwordActionListener) {
    this.passwordActionListener = passwordActionListener;
  }

  public boolean isSafe() {
    return this.safe;
  }

  public void isSafe(boolean safe) {
    this.safe = safe;
  }

  public static List<Password> getPasswords(Cursor cursor) {
    List<Password> passwords = new ArrayList<>();
    Password password = new Password(-1, -1, null, null);

    if (cursor.moveToNext())
      password = Password.getFromCursor(cursor);

    while (cursor.moveToNext()) {
      Password nextPassword = Password.getFromCursor(cursor);

      if (nextPassword.equals(password)) {
        password.merge(nextPassword);
      } else {
        password.orderHistoryByDate();
        passwords.add(password);

        password = nextPassword;
      }
    }

    password.orderHistoryByDate();
    passwords.add(password);
    cursor.close();

    return passwords;
  }

  public interface PasswordActionListener {
    void onPasswordAdded(Password password);

    void onPasswordRemoved(Password password);

    void onPasswordEdited(Password password, PasswordHistory history);

    void onOrder();
  }

  private class PasswordComparator implements Comparator<Password> {
    private Method method;
    private boolean inverted;

    public PasswordComparator(String method, boolean inverted) {
      try {
        this.inverted = inverted;
        this.method = Password.class.getDeclaredMethod(method);
      } catch (NoSuchMethodException e) {
        Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
      }
    }

    @Override
    public int compare(Password lhs, Password rhs) {
      try {
        String lhsString = ((String) (inverted ? method.invoke(lhs) : method.invoke(rhs))).toLowerCase();
        String rhsString = ((String) (inverted ? method.invoke(rhs) : method.invoke(lhs))).toLowerCase();

        int compareTo = lhsString.compareTo(rhsString);
        if (compareTo != 0)
          lhs.swapPositionWith(rhs);

        return compareTo;
      } catch (IllegalAccessException | InvocationTargetException e) {
        return 0;
      }
    }
  }
}
