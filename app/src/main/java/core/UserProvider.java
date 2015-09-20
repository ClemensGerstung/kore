package core;

import android.content.Context;
import android.database.Cursor;
import core.exceptions.UserProviderException;

import java.security.NoSuchAlgorithmException;

public class UserProvider {
    private static UserProvider INSTANCE;

    private Context context;
    private User currentUser;
    private String username;

    public static UserProvider getInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = new UserProvider(context);
        }
        return INSTANCE;
    }

    private UserProvider(Context context) {
        this.context = context;
        this.currentUser = null;
    }

    public User login(String passwordHash, String salt, boolean safeLogin) throws UserProviderException {
        Cursor cursor = DatabaseProvider.getConnection(context).query(DatabaseProvider.GET_USER_ID, username, passwordHash);
        if(cursor.getCount() == 0) {
            throw new UserProviderException("Your login credentials are wrong");
        }

        int id = -1;

        while (cursor.moveToNext()) {
            id = cursor.getInt(0);
        }

        cursor.close();
        DatabaseProvider.dismiss();
        if(currentUser == null) {
            currentUser = new User(id, username, salt, passwordHash);
        }
        return currentUser;
    }

    public boolean userExists(String username) {
        Cursor cursor = DatabaseProvider.getConnection(context).query(DatabaseProvider.DOES_USER_EXISTS, username);

        int i = 0;
        while (cursor.moveToNext()) {
            i = cursor.getInt(0);
        }

        cursor.close();
        DatabaseProvider.dismiss();
        return i == 1;
    }

    public User createUser(String username, String password, String salt, boolean autoLogin) throws UserProviderException {
        if(userExists(username)) {
            throw new UserProviderException("Your username already exists");
        }

        String passwordHash = null;
        try {
            passwordHash = Utils.getHashedString(password + Utils.getHashedString(salt));
        } catch (NoSuchAlgorithmException e) {
            throw new UserProviderException("Something went wrong by creating the user!");
        }

        long id = DatabaseProvider.getConnection(context).insert(DatabaseProvider.CREATE_USER, username, passwordHash, salt);
        if(autoLogin) {
            setUsername(username);
            User user = login(passwordHash, salt, false);
            user.setPlainPassword(password);
            return user;
        }

        if(currentUser == null) {
            currentUser = new User((int) id, username, password, salt, passwordHash);
        }
        return currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
