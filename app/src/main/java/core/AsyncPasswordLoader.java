package core;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

public class AsyncPasswordLoader extends AsyncTaskLoader<Password> {
    private String query;
    private String[] args;

    private ItemAddCallback callback;

    public AsyncPasswordLoader(Context context, String query, String... args) {
        super(context);
        this.query = query;
        this.args = args;
    }


    @Override
    public Password loadInBackground() {
        Cursor cursor = DatabaseProvider.getConnection(getContext()).query(query, args);

        Password lastPassword = null;
        Password password = null;
        while (cursor.moveToNext()) {
            int passwordId = cursor.getInt(0);
            int position = cursor.getInt(1);
            String program = cursor.getString(2);
            String username = cursor.getString(3);
            int historyId = cursor.getInt(4);
            String value = cursor.getString(5);
            String dateChanged = cursor.getString(6);

            if (lastPassword == null) {
                lastPassword = new Password(passwordId, position, username, program);
                lastPassword.addHistoryItem(historyId, value, dateChanged);
            } else {
                password = new Password(passwordId, position, username, program);
                if (lastPassword.equals(password)) {
                    lastPassword.addHistoryItem(historyId, value, dateChanged);
                } else {
                    if (callback != null) {
                        callback.itemAdded(lastPassword);
                    }

                    lastPassword = new Password(passwordId, position, username, program);
                    lastPassword.addHistoryItem(historyId, value, dateChanged);
                }
            }
        }

        return password;
    }

    public void setItemAddCallback(ItemAddCallback callback) {
        this.callback = callback;
    }

    public interface ItemAddCallback {
        void itemAdded(Password password);
    }
}