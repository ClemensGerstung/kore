package core.data;

import android.content.Context;
import android.support.annotation.Nullable;
import core.DatabaseProvider;
import core.exceptions.UserProviderException;

import java.util.*;

public class PasswordProvider {
  private static PasswordProvider Instance;

  private Context context;
  private List<Password> passwords;
  private Transactions transactions;
  private PasswordActionListener passwordActionListener;

  private PasswordProvider(Context context) {
    this.context = context;
    this.passwords = new ArrayList<>();
    this.transactions = new Transactions();
  }

  public static PasswordProvider getInstance(Context context) {
    if (Instance == null)
      Instance = new PasswordProvider(context);
    return Instance;
  }

  public void set(Password password) {
    int index = passwords.indexOf(password);
    passwords.set(index, password);
  }

  public void remove(Password password) {
    passwords.remove(password);
  }

  public int size() {
    return passwords.size();
  }

  public Password get(int index) {
    return passwords.get(index);
  }

  public Password getById(int id) {
    for (Password password : passwords) {
      if (password.getId() == id) {
        return password;
      }
    }
    return null;
  }

  public boolean contains(Password p) {
    return passwords.contains(p);
  }

  public static void logoutComplete() {
    Instance.simpleLogout();
    Instance.context = null;
  }

  void simpleLogout() {
    for (Password p : passwords) {
      p.logout();
    }
    passwords.clear();
  }

  List<Password> getPasswords() {
    return passwords;
  }

  public void order(int which) {
    if (which == 0) {   // order by username ascending
      Collections.sort(passwords, new Comparator<Password>() {
        @Override
        public int compare(Password lhs, Password rhs) {
          int compareTo = lhs.getUsername().compareTo(rhs.getUsername());
          if (compareTo != 0)
            lhs.swapPositionWith(rhs);

          return compareTo;
        }
      });
    } else if (which == 1) {    // order by username descending
      Collections.sort(passwords, new Comparator<Password>() {
        @Override
        public int compare(Password lhs, Password rhs) {
          int compareTo = ~lhs.getUsername().compareTo(rhs.getUsername());
          if (compareTo != 0)
            lhs.swapPositionWith(rhs);

          return compareTo;
        }
      });
    } else if (which == 2) {   // order by password ascending
      Collections.sort(passwords, new Comparator<Password>() {
        @Override
        public int compare(Password lhs, Password rhs) {
          int compareTo = lhs.getFirstItem().compareTo(rhs.getFirstItem());
          if (compareTo != 0)
            lhs.swapPositionWith(rhs);

          return compareTo;
        }
      });
    } else if (which == 3) {    // order by password descending
      Collections.sort(passwords, new Comparator<Password>() {
        @Override
        public int compare(Password lhs, Password rhs) {
          int compareTo = ~lhs.getFirstItem().compareTo(rhs.getFirstItem());
          if (compareTo != 0)
            lhs.swapPositionWith(rhs);

          return compareTo;
        }
      });
    } else if (which == 4) {   // order by program ascending
      Collections.sort(passwords, new Comparator<Password>() {
        @Override
        public int compare(Password lhs, Password rhs) {
          int compareTo = lhs.getProgram().compareTo(rhs.getProgram());
          if (compareTo != 0)
            lhs.swapPositionWith(rhs);

          return compareTo;
        }
      });
    } else if (which == 5) {    // order by program descending
      Collections.sort(passwords, new Comparator<Password>() {
        @Override
        public int compare(Password lhs, Password rhs) {
          int compareTo = ~lhs.getProgram().compareTo(rhs.getProgram());
          if (compareTo != 0)
            lhs.swapPositionWith(rhs);

          return compareTo;
        }
      });
    }


  }

  public Password addPassword(String program, String username, String password) throws Exception {
    int id = transactions.getNewPasswordId();
    int historyId = transactions.getNewHistoryId();

    int position = passwords.size() + 1;
    Password passwordObject = new Password(id, position, username, program);
    passwordObject.addPasswordHistoryItem(id, PasswordHistory.createItem(password));


    return addPassword(passwordObject);
  }

  public Password addPassword(Password password) throws Exception {

    if (!contains(password)) {
      passwords.add(password);
      transactions.setIdsFromPassword(password);
    }

    if (passwordActionListener != null)
      passwordActionListener.onPasswordAdded(password);

    return password;
  }

  public void editPassword(int id, String newPassword) throws Exception {
    DatabaseProvider provider = DatabaseProvider.getConnection(context);

    PasswordHistory history = PasswordHistory.createItem(newPassword);
    //String json = history.getJson();
        /*String encryptedJson = AesProvider.encrypt(json, currentUser.plainPassword);

        long historyId = provider.insert(DatabaseProvider.INSERT_NEW_HISTORY_ITEM, encryptedJson);

        if (historyId == -1)
            throw new UserProviderException("Couldn't insert your password history item!");

        Password password = passwordProvider.getById(id);
        password.addPasswordHistoryItem((int) historyId, history);

        editPassword(password);*/
  }

  public void editPassword(int id, @Nullable String program, @Nullable String username) throws Exception {
    Password password = getById(id);
    password.setUsername(username);
    password.setProgram(program);

    editPassword(password);
  }

  public void editPassword(Password password) throws Exception {
    set(password);

        /*String json = password.getJson();
        String encrypted = AesProvider.encrypt(json, currentUser.plainPassword);

        DatabaseProvider.getConnection(context).update(DatabaseProvider.UPDATE_PASSWORD_BY_ID, encrypted, Integer.toString(password.getId()));
*/
    if (passwordActionListener != null)
      passwordActionListener.onPasswordEdited(password, password.getFirstHistoryItem());

  }

  public void removePassword(Password password) {
    DatabaseProvider provider = DatabaseProvider.getConnection(context);
    /*
    for (Integer i : password.getPasswordIds()) {
      provider.remove(DatabaseProvider.DELETE_PASSWORDHISTORY_BY_ID, i.toString());
    }

    provider.remove(DatabaseProvider.DELETE_PASSWORD_BY_ID, Integer.toString(password.getId()));

    DatabaseProvider.dismiss();

    passwordProvider.remove(password);
    */
    if (passwordActionListener != null)
      passwordActionListener.onPasswordRemoved(password);
  }

  public void setPasswordActionListener(PasswordActionListener passwordActionListener) {
    this.passwordActionListener = passwordActionListener;
  }

  public boolean isSafe() {
    return false;
  }

  public interface PasswordActionListener {
    void onPasswordAdded(Password password);

    void onPasswordRemoved(Password password);

    void onPasswordEdited(Password password, PasswordHistory history);
  }
}
