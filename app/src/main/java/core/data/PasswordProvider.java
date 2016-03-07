package core.data;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import core.DatabaseProvider;
import core.Dictionary;
import core.async.AsyncDatabasePipeline;
import core.exceptions.PasswordProviderException;
import net.sqlcipher.Cursor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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

    for (Password password : passwords) {
      boolean add = false;
      for (Password existing : this.passwords) {
        if (password.getUsername().equals(existing.getUsername())
            && password.getProgram().equals(existing.getProgram())) {

          add = true;
          merged++;
          break;
        }
      }

      if (!add)
        addPassword(password);
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

  public Password addPassword(String program, String username, String password) throws Exception {
    int position = passwords.size() + 1;

    DatabaseProvider provider = DatabaseProvider.getConnection(context);
    long passwordId = provider.insert(DatabaseProvider.INSERT_NEW_PASSWORD, username, program, position);
    if (passwordId < 0)
      throw new PasswordProviderException("Couldn't insert your password");

    long historyId = provider.insert(DatabaseProvider.INSERT_NEW_HISTORY, password, passwordId);
    if (historyId < 0)
      throw new PasswordProviderException("Couldn't insert your password!");

    Password passwordObject = new Password((int) passwordId, position, username, program);
    passwordObject.addPasswordHistoryItem((int) historyId, PasswordHistory.createItem(password));

    return addPassword(passwordObject);
  }

  public Password addPassword(Password password) {

    password.orderHistoryByDate();

    if (!passwords.contains(password))
      passwords.add(password);

    if (passwordActionListener != null)
      passwordActionListener.onPasswordAdded(password);

    return password;
  }

  public int editPassword(final int id, String newPassword) throws Exception {
    final PasswordHistory history = PasswordHistory.createItem(newPassword);

    AsyncDatabasePipeline.AsyncQueryListener listener = new AsyncDatabasePipeline.AsyncQueryListener() {
      @Override
      public void executed(Object... results) {
        if (!(results[0] instanceof Long))
          return;

        long historyId = (Long) results[0];
        Password password = getById(id);
        password.addPasswordHistoryItem((int) historyId, history);

        editPassword(password);
      }

      @Override
      public void failed(String message) {
      }
    };

    AsyncDatabasePipeline.getPipeline(context).addQuery(DatabaseProvider.INSERT_NEW_HISTORY, listener, newPassword, id);

    return 0;
  }



  public void editPassword(int id, @Nullable String program, @Nullable String username) {
    Password password = getById(id);
    password.setUsername(username);
    password.setProgram(program);

    AsyncDatabasePipeline.getPipeline(context).addQuery(DatabaseProvider.UPDATE_PASSWORD_BY_ID, null, program, username, id);

    editPassword(password);
  }

  void editPassword(Password password) {
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
      AsyncDatabasePipeline.getPipeline(context).addQuery(DatabaseProvider.REMOVE_HISTORY_BY_ID, null, i);
      //provider.remove(DatabaseProvider.REMOVE_HISTORY_BY_ID, i);
    }

    //provider.remove(DatabaseProvider.REMOVE_PASSWORD_BY_ID, password.getId());

    AsyncDatabasePipeline.getPipeline(context).addQuery(DatabaseProvider.REMOVE_PASSWORD_BY_ID, null, password.getId());

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
