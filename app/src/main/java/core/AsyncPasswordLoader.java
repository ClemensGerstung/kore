package core;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

public class AsyncPasswordLoader extends AsyncTask<String, Void, Password> {
    private String query;
    private String[] args;
    private Context context;

    private ItemAddCallback callback;

    public AsyncPasswordLoader(Context context, String query, String... args) {
        super();
        this.query = query;
        this.args = args;
        this.context = context;
    }

    @Override
    protected Password doInBackground(String... params) {
        Cursor cursor = DatabaseProvider.getConnection(context).query(query, args);

        Password passwordToAdd = null;
        Password tmpPassword = null;
        while (cursor.moveToNext()) {

            int passwordId = cursor.getInt(0);
            int position = cursor.getInt(1);
            String program = cursor.getString(2);
            String username = cursor.getString(3);
            int historyId = cursor.getInt(4);
            String value = cursor.getString(5);
            String dateChanged = cursor.getString(6);

            if (passwordToAdd == null) {
                passwordToAdd = new Password(passwordId, position, username, program);
                passwordToAdd.addHistoryItem(historyId, value, dateChanged);
            } else {
                tmpPassword = new Password(passwordId, position, username, program);
                if (passwordToAdd.equals(tmpPassword)) {
                    passwordToAdd.addHistoryItem(historyId, value, dateChanged);
                } else {
                    if (callback != null) {
                        callback.itemAdded(passwordToAdd);
                    }

                    passwordToAdd = new Password(passwordId, position, username, program);
                    passwordToAdd.addHistoryItem(historyId, value, dateChanged);
                }
            }
        }

        if (callback != null) {
            callback.itemAdded(passwordToAdd);
        }

        return passwordToAdd;
    }

    public void setItemAddCallback(ItemAddCallback callback) {
        this.callback = callback;
    }


    public interface ItemAddCallback {
        void itemAdded(Password password);
    }
}