package core;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import core.data.Password;
import core.data.PasswordHistory;
import core.data.User;
import core.data.UserProvider;

public class AsyncPasswordLoader extends AsyncTask<String, Void, Void> {
    private Context context;

    public AsyncPasswordLoader(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        UserProvider provider = UserProvider.getInstance(context);
        provider.clearPasswords();
        User currentUser = provider.getCurrentUser();
        try {
            DatabaseProvider connection = DatabaseProvider.getConnection(context);

            for (Integer passwordId : currentUser.getPasswordIds()) {
                Cursor cursor = connection.query(DatabaseProvider.GET_PASSWORD_BY_ID, passwordId.toString());

                if (!cursor.moveToNext())
                    continue;

                int id = cursor.getInt(0);
                String dbJson = cursor.getString(1);
                String passwordJson = UserProvider.decrypt(dbJson);
                Password password = Password.getFromJson(id, passwordJson);

                for (int i = password.getPasswordIds().size() - 1; i >= 0; i--) {
                    Integer passwordHistoryId = password.getKeyAt(i);

                    cursor = connection.query(DatabaseProvider.GET_HISTORYITEM_BY_ID, passwordHistoryId.toString());
                    if (!cursor.moveToNext())
                        continue;

                    dbJson = cursor.getString(0);
                    String pwHistoryJson = UserProvider.decrypt(dbJson);
                    PasswordHistory history = PasswordHistory.getFromJson(pwHistoryJson);
                    password.setPasswordHistoryItem(passwordHistoryId, history);
                }

                Log.d(getClass().getSimpleName(), password.toString());
                provider.addPassword(password);
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
        }

        return null;
    }
}