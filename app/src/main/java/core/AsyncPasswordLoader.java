package core;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import java.util.Collections;

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
        String userMasterPassword = UserProvider.getInstance(context).getCurrentUser().getPlainPassword();

        Password passwordToAdd = null;
        Password tmpPassword;

        int passwordId;
        int position;
        String program;
        String username;
        int historyId;
        String value;
        String dateChanged;
        String programDecrypted;
        String usernameDecrypted;
        String valueDecrypted;
        String dateChangedDecrypted;

        while (cursor.moveToNext()) {

            passwordId = cursor.getInt(0);
            position = cursor.getInt(1);
            program = cursor.getString(2);
            username = cursor.getString(3);
            historyId = cursor.getInt(4);
            value = cursor.getString(5);
            dateChanged = cursor.getString(6);

            try {
                programDecrypted = AesProvider.decrypt(program, userMasterPassword);
                usernameDecrypted = AesProvider.decrypt(username, userMasterPassword);
                valueDecrypted = AesProvider.decrypt(value, userMasterPassword);
                dateChangedDecrypted = AesProvider.decrypt(dateChanged, userMasterPassword);
            } catch (Exception e) {
                return null;
            }

            if (passwordToAdd == null) {
                passwordToAdd = new Password(passwordId, position, usernameDecrypted, programDecrypted);
                passwordToAdd.addHistoryItem(historyId, valueDecrypted, dateChangedDecrypted);
            } else {
                tmpPassword = new Password(passwordId, position, usernameDecrypted, programDecrypted);
                if (passwordToAdd.equals(tmpPassword)) {
                    passwordToAdd.addHistoryItem(historyId, valueDecrypted, dateChangedDecrypted);
                } else {
                    if (callback != null) {
                        Collections.reverse(passwordToAdd.getPasswordHistory());
                        callback.itemAdded(passwordToAdd);
                    }

                    passwordToAdd = new Password(passwordId, position, usernameDecrypted, programDecrypted);
                    passwordToAdd.addHistoryItem(historyId, valueDecrypted, dateChangedDecrypted);
                }
            }
        }

        if (callback != null && passwordToAdd != null) {
            Collections.reverse(passwordToAdd.getPasswordHistory());
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