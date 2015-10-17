package core;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import core.exceptions.PasswordProviderException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PasswordProvider {
    private static PasswordProvider INSTANCE;


    private Context context;
    private int userId;
    private List<Password> passwords;

    private OnPasswordAddedToDatabase onPasswordAddedToDatabase;

    public PasswordProvider(Context context, int userId) {
        this.context = context;
        this.userId = userId;
        this.passwords = new ArrayList<>();
    }

    public static PasswordProvider getInstance(Context context, int userId) {
        if (INSTANCE == null) {
            INSTANCE = new PasswordProvider(context, userId);
        }
        return INSTANCE;
    }

    public static PasswordProvider getInstance() {
        if (INSTANCE == null) {
            throw new NullPointerException("you have to call getInstance(Context, int) first");
        }
        return INSTANCE;
    }

    public int insertIntoDatabase(String program, String username, String password) throws Exception {
        int position = 0;
        int passwordId = -1;
        DatabaseProvider connection = DatabaseProvider.getConnection(context);
        String date = Utils.getDate();
        String userMasterPassword = UserProvider.getInstance(context).getCurrentUser().getPlainPassword();
        String encryptedProgram = AesProvider.encrypt(program, userMasterPassword);
        String encryptedUsername = AesProvider.encrypt(username, userMasterPassword);
        String encryptedPassword = AesProvider.encrypt(password, userMasterPassword);
        String encryptedDate = AesProvider.encrypt(date, userMasterPassword);

        String userIdString = Integer.toString(userId);
        Cursor cursor = connection.query(DatabaseProvider.GET_MAX_POSITION, userIdString);
        if (cursor.moveToNext()) {
            position = cursor.getInt(0);
        }

        passwordId = (int) connection.insert(DatabaseProvider.INSERT_PASSWORD, encryptedUsername, encryptedProgram, Integer.toString(position), userIdString);

        if (passwordId == -1) {
            throw new PasswordProviderException("Couldn't insert your password");
        }

        int historyId = insertPasswordHistoryItem(encryptedPassword, encryptedDate, passwordId);

        if (onPasswordAddedToDatabase != null) {
            onPasswordAddedToDatabase.onPasswordAdded(passwordId, historyId);
        }

        Password passwordObj = new Password(passwordId, position, username, program);
        passwordObj.addHistoryItem(historyId, password, date);
        passwords.add(passwordObj);

        return passwordId;
    }

    public void add(Password password) {
        passwords.add(password);

        if (onPasswordAddedToDatabase != null) {
            PasswordHistory history = password.getPasswordHistory().get(0);
            onPasswordAddedToDatabase.onPasswordAdded(password.getId(), history.getId());
        }
    }

    private int insertPasswordHistoryItem(String value, String date, int passwordId) throws PasswordProviderException {
        int historyId = -1;
        DatabaseProvider connection = DatabaseProvider.getConnection(context);

        historyId = (int) connection.insert(DatabaseProvider.INSERT_HISTORY_FOR_PASSWORD, value, date, Integer.toString(passwordId));

        if (historyId == -1) {
            throw new PasswordProviderException("Couldn't insert your password");
        }

        return historyId;
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

    public void update(int id, @Nullable String username, @Nullable String program) throws PasswordProviderException {
        DatabaseProvider provider = DatabaseProvider.getConnection(context);
        int returnedValue = -1;
        if (username != null && program != null) {
            returnedValue = provider.update(DatabaseProvider.UPDATE_USERNAME_AND_PASSWORD, username, program, Integer.toString(id));
        } else if (username != null && program == null) {
            returnedValue = provider.update(DatabaseProvider.UPDATE_USERNAME, username, Integer.toString(id));
        } else if (program != null && username == null) {
            returnedValue = provider.update(DatabaseProvider.UPDATE_PROGRAM, program, Integer.toString(id));
        } else {
            throw new PasswordProviderException("Not supported operation: either username or password has to be something");
        }

        if (returnedValue == -1) {
            throw new PasswordProviderException("There was an error by updating this password");
        }

        provider.close();
    }

    public void addPasswordHistoryItem(int id, String password) throws Exception {
        String userMasterPassword = UserProvider.getInstance(context).getCurrentUser().getPlainPassword();
        String date = Utils.getDate();
        String encryptedPassword = AesProvider.encrypt(password, userMasterPassword);
        String encryptedDate = AesProvider.encrypt(date, userMasterPassword);

        DatabaseProvider provider = DatabaseProvider.getConnection(context);

        insertPasswordHistoryItem(encryptedPassword, encryptedDate, id);
    }

    public boolean contains(Password p) {
        return passwords.contains(p);
    }

    public void setOnPasswordAddedToDatabase(OnPasswordAddedToDatabase onPasswordAddedToDatabase) {
        this.onPasswordAddedToDatabase = onPasswordAddedToDatabase;
    }

    public interface OnPasswordAddedToDatabase {
        void onPasswordAdded(int passwordId, int historyId);
    }

    public void order(int which) {
        if (which == 0) {   // order by username ascending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    return lhs.getUsername().compareTo(rhs.getUsername());
                }
            });
        } else if (which == 1) {    // order by username descending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    return ~lhs.getUsername().compareTo(rhs.getUsername());
                }
            });
        } else if (which == 2) {   // order by password ascending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    return lhs.getFirstItem().getValue().compareTo(rhs.getFirstItem().getValue());
                }
            });
        } else if (which == 3) {    // order by password descending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    return ~lhs.getFirstItem().getValue().compareTo(rhs.getFirstItem().getValue());
                }
            });
        } else if (which == 4) {   // order by program ascending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    return lhs.getProgram().compareTo(rhs.getProgram());
                }
            });
        } else if (which == 5) {    // order by program descending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    return ~lhs.getProgram().compareTo(rhs.getProgram());
                }
            });
        }
    }
}
