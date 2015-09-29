package core;

import android.content.Context;
import android.database.Cursor;
import core.exceptions.PasswordProviderException;

import java.util.ArrayList;
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

    public int insertIntoDatabase(String program, String username, String password) throws PasswordProviderException {
        int position = 0;
        int passwordId = -1;
        DatabaseProvider connection = DatabaseProvider.getConnection(context);

        String userIdString = Integer.toString(userId);
        Cursor cursor = connection.query(DatabaseProvider.GET_MAX_POSITION, userIdString);
        if (cursor.moveToNext()) {
            position = cursor.getInt(0);
        }

        passwordId = (int) connection.insert(DatabaseProvider.INSERT_PASSWORD, username, program, Integer.toString(position), userIdString);

        if (passwordId == -1) {
            throw new PasswordProviderException("Couldn't insert your password");
        }

        int historyId = insertPasswordHistoryItem(password, passwordId);

        if (onPasswordAddedToDatabase != null) {
            onPasswordAddedToDatabase.onPasswordAdded(passwordId, historyId);
        }

        return passwordId;
    }

    public void add(Password password) {
        passwords.add(password);

        if (onPasswordAddedToDatabase != null) {
            PasswordHistory history = password.getPasswordHistory().get(0);
            onPasswordAddedToDatabase.onPasswordAdded(password.getId(), history.getId());
        }
    }

    public int insertPasswordHistoryItem(String value, int passwordId) throws PasswordProviderException {
        int historyId = -1;
        DatabaseProvider connection = DatabaseProvider.getConnection(context);

        historyId = (int) connection.insert(DatabaseProvider.INSERT_HISTORY_FOR_PASSWORD, value, Utils.getDate(), Integer.toString(passwordId));

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

    public boolean contains(Password p) {
        return passwords.contains(p);
    }

    public void setOnPasswordAddedToDatabase(OnPasswordAddedToDatabase onPasswordAddedToDatabase) {
        this.onPasswordAddedToDatabase = onPasswordAddedToDatabase;
    }

    public interface OnPasswordAddedToDatabase {
        void onPasswordAdded(int passwordId, int historyId);
    }
}
