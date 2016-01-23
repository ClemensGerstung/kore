package core.data;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import core.DatabaseProvider;
import core.Dictionary;
import core.exceptions.PasswordProviderException;
import core.exceptions.UserProviderException;
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

  public int merge(Cursor cursor) {
    int merged = 0;

    if (!cursor.moveToNext())
      return 0;
    Password password = Password.getFromCursor(cursor);

    while (cursor.moveToNext()) {
      Password nextPassword = Password.getFromCursor(cursor);

      if (nextPassword.equals(password)) {
        password.merge(nextPassword);
      } else {
        mergeOrAdd(password);

        merged++;
        password.orderHistoryByDate();
        password = nextPassword;
      }
    }

    mergeOrAdd(password);
    merged++;

    return merged;
  }

  private void mergeOrAdd(Password password) {
    boolean contains = false;

    for (Password existingPassword : passwords) {
      contains = false;
      if (!existingPassword.simpleEquals(password))
        continue;

      Collection<PasswordHistory> values = password.getPasswordHistory().values();
      for (PasswordHistory value : values) {
        if (existingPassword.getPasswordHistory().containsValue(value, Dictionary.IterationOption.Forwards))
          continue;
        addHistoryItemAsync(existingPassword, value);
      }
      contains = true;
      break;
    }

    if (!contains) {
      addPassword(password);
    }
  }

  private void addHistoryItemAsync(Password password, PasswordHistory item) {

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

  public int editPassword(int id, String newPassword) throws Exception {
    DatabaseProvider provider = DatabaseProvider.getConnection(context);

    PasswordHistory history = PasswordHistory.createItem(newPassword);

    long historyId = provider.insert(DatabaseProvider.INSERT_NEW_HISTORY, newPassword, id);

    if (historyId == -1)
      throw new UserProviderException("Couldn't insert your password history item!");

    Password password = getById(id);
    password.addPasswordHistoryItem((int) historyId, history);

    editPassword(password);

    return (int) historyId;
  }

  public void editPassword(int id, @Nullable String program, @Nullable String username) {
    Password password = getById(id);
    password.setUsername(username);
    password.setProgram(program);

    DatabaseProvider.getConnection(context)
        .update(DatabaseProvider.UPDATE_PASSWORD_BY_ID, program, username, id);

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

  public Password removePassword(Password password) {
    DatabaseProvider provider = DatabaseProvider.getConnection(context);

    List<Integer> list = new ArrayList<>(password.getPasswordIds());
    for (Integer i : list) {
      provider.remove(DatabaseProvider.REMOVE_HISTORY_BY_ID, i);
    }

    provider.remove(DatabaseProvider.REMOVE_PASSWORD_BY_ID, password.getId());

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
