package core;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import core.data.Password;
import core.data.User;
import core.data.UserProvider;

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
        User currentUser = UserProvider.getInstance(context).getCurrentUser();
        try {
            for(Integer i : currentUser.getPasswordIds()) {
                Cursor passwordCursor = DatabaseProvider.getConnection(context).query(query, i.toString());
                if(passwordCursor.moveToNext()) {
                    String dbJson = passwordCursor.getString(0);
                    String passwordJson = UserProvider.decrypt(dbJson);
                    Password password = Password.getFromJson(passwordJson);
                }
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
        }


//
//        Password passwordToAdd = null;
//        Password tmpPassword;
//
//        int passwordId;
//        int position;
//        String program;
//        String username;
//        int historyId;
//        String value;
//        String dateChanged;
//        String programDecrypted;
//        String usernameDecrypted;
//        String valueDecrypted;
//        String dateChangedDecrypted;
//
//        while (cursor.moveToNext()) {
//
//            passwordId = cursor.getInt(0);
//            position = cursor.getInt(1);
//            program = cursor.getString(2);
//            username = cursor.getString(3);
//            historyId = cursor.getInt(4);
//            value = cursor.getString(5);
//            dateChanged = cursor.getString(6);
//
//            try {
//                programDecrypted = AesProvider.decrypt(program, userMasterPassword);
//                usernameDecrypted = AesProvider.decrypt(username, userMasterPassword);
//                valueDecrypted = AesProvider.decrypt(value, userMasterPassword);
//                dateChangedDecrypted = AesProvider.decrypt(dateChanged, userMasterPassword);
//            } catch (Exception e) {
//                continue;
//            }
//
//            if (passwordToAdd == null) {
//                passwordToAdd = new Password(passwordId, position, usernameDecrypted, programDecrypted);
//                passwordToAdd.addHistoryItem(historyId, valueDecrypted, dateChangedDecrypted);
//            } else {
//                tmpPassword = new Password(passwordId, position, usernameDecrypted, programDecrypted);
//                if (passwordToAdd.equals(tmpPassword)) {
//                    passwordToAdd.addHistoryItem(historyId, valueDecrypted, dateChangedDecrypted);
//                } else {
//                    if (callback != null) {
//                        Collections.reverse(passwordToAdd.getPasswordHistory());
//                        callback.itemAdded(passwordToAdd);
//                    }
//
//                    passwordToAdd = new Password(passwordId, position, usernameDecrypted, programDecrypted);
//                    passwordToAdd.addHistoryItem(historyId, valueDecrypted, dateChangedDecrypted);
//                }
//            }
//        }
//
//        if (callback != null && passwordToAdd != null) {
//            Collections.reverse(passwordToAdd.getPasswordHistory());
//            callback.itemAdded(passwordToAdd);
//        }
//
//        return passwordToAdd;
        return null;
    }

    public void setItemAddCallback(ItemAddCallback callback) {
        this.callback = callback;
    }

    public interface ItemAddCallback {
        void itemAdded(Password password);
    }
}