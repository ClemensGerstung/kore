package core.data;

import android.content.Context;
import android.support.annotation.Nullable;
import core.AesProvider;
import core.DatabaseProvider;
import core.exceptions.PasswordProviderException;
import core.exceptions.UserProviderException;

import java.util.*;

public class PasswordProvider {
    private static PasswordProvider Instance;

    private Context context;
    private List<Password> passwords;


    PasswordProvider(Context context) {
        this.context = context;
        this.passwords = new ArrayList<>();
    }

    public static PasswordProvider getInstance(Context context) {
        if(Instance == null)
            Instance = new PasswordProvider(context);
        return Instance;
    }

    public void add(Password password) {
        if(password.getPosition() == Integer.MIN_VALUE)
            password.setPosition(passwords.size() + 1);

        passwords.add(password);
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

    void logoutComplete() {
        simpleLogout();
        context = null;
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
                    if(compareTo != 0)
                        lhs.swapPositionWith(rhs);

                    return compareTo;
                }
            });
        } else if (which == 1) {    // order by username descending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    int compareTo = ~lhs.getUsername().compareTo(rhs.getUsername());
                    if(compareTo != 0)
                        lhs.swapPositionWith(rhs);

                    return compareTo;
                }
            });
        } else if (which == 2) {   // order by password ascending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    int compareTo = lhs.getFirstItem().compareTo(rhs.getFirstItem());
                    if(compareTo != 0)
                        lhs.swapPositionWith(rhs);

                    return compareTo;
                }
            });
        } else if (which == 3) {    // order by password descending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    int compareTo = ~lhs.getFirstItem().compareTo(rhs.getFirstItem());
                    if(compareTo != 0)
                        lhs.swapPositionWith(rhs);

                    return compareTo;
                }
            });
        } else if (which == 4) {   // order by program ascending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    int compareTo = lhs.getProgram().compareTo(rhs.getProgram());
                    if(compareTo != 0)
                        lhs.swapPositionWith(rhs);

                    return compareTo;
                }
            });
        } else if (which == 5) {    // order by program descending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    int compareTo = ~lhs.getProgram().compareTo(rhs.getProgram());
                    if(compareTo != 0)
                        lhs.swapPositionWith(rhs);

                    return compareTo;
                }
            });
        }


    }

    public void addPassword(String program, String username, String password) throws Exception {
        DatabaseProvider provider = DatabaseProvider.getConnection(context);

        PasswordHistory history = PasswordHistory.createItem(password);
        String json = history.getJson();
        String encryptedJson = AesProvider.encrypt(json, currentUser.plainPassword);

        long historyId = provider.insert(DatabaseProvider.INSERT_NEW_HISTORY_ITEM, encryptedJson);

        if (historyId == -1)
            throw new UserProviderException("Couldn't insert your password history item!");

        Password passwordObject = Password.createSimplePassword(program, username);
        passwordObject.setPasswordHistoryItem((int) historyId, history);
        json = passwordObject.getJson();
        encryptedJson = AesProvider.encrypt(json, currentUser.plainPassword);
        long id = provider.insert(DatabaseProvider.INSERT_NEW_PASSWORD, encryptedJson);

        if (id == -1)
            throw new UserProviderException("Couldn't insert your password!");

        passwordObject.setId((int) id);
        addPassword(passwordObject);
    }

    public void addPassword(Password password) throws Exception {

        if (!passwordProvider.contains(password))
            passwordProvider.add(password);

        if (password.hasId() && !currentUser.hasPassword(password.getId())) {

            currentUser.addPasswordById(password.getId());

            String json = currentUser.getPasswordsAsJson();
            String encryptedJson = AesProvider.encrypt(json, currentUser.plainPassword);

            long effectedRows = DatabaseProvider.getConnection(context)
                    .update(DatabaseProvider.UPDATE_PASSWORDIDS_FOR_USER, encryptedJson, Integer.toString(currentUser.getId()));
        }

        if (passwordActionListener != null)
            passwordActionListener.onPasswordAdded(password);
    }

    public void editPassword(int id, String newPassword) throws Exception {
        DatabaseProvider provider = DatabaseProvider.getConnection(context);

        PasswordHistory history = PasswordHistory.createItem(newPassword);
        String json = history.getJson();
        String encryptedJson = AesProvider.encrypt(json, currentUser.plainPassword);

        long historyId = provider.insert(DatabaseProvider.INSERT_NEW_HISTORY_ITEM, encryptedJson);

        if (historyId == -1)
            throw new UserProviderException("Couldn't insert your password history item!");

        Password password = passwordProvider.getById(id);
        password.addPasswordHistoryItem((int) historyId, history);

        editPassword(password);
    }

    public void editPassword(int id, @Nullable String program, @Nullable String username) throws Exception {
        Password password = passwordProvider.getById(id);
        password.setUsername(username);
        password.setProgram(program);

        editPassword(password);
    }

    public void editPassword(Password password) throws Exception {
        passwordProvider.set(password);

        String json = password.getJson();
        String encrypted = AesProvider.encrypt(json, currentUser.plainPassword);

        DatabaseProvider.getConnection(context).update(DatabaseProvider.UPDATE_PASSWORD_BY_ID, encrypted, Integer.toString(password.getId()));

        if (passwordActionListener != null)
            passwordActionListener.onPasswordEdited(password, password.getFirstHistoryItem());

    }

    public void removePassword(Password password) {
        DatabaseProvider provider = DatabaseProvider.getConnection(context);

        for (Integer i : password.getPasswordIds()) {
            provider.remove(DatabaseProvider.DELETE_PASSWORDHISTORY_BY_ID, i.toString());
        }

        provider.remove(DatabaseProvider.DELETE_PASSWORD_BY_ID, Integer.toString(password.getId()));

        DatabaseProvider.dismiss();

        passwordProvider.remove(password);

        if (passwordActionListener != null)
            passwordActionListener.onPasswordRemoved(password);
    }


}
