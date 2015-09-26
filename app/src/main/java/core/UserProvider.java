package core;

import android.content.Context;
import android.database.Cursor;
import android.os.RemoteException;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import core.exceptions.LoginException;
import core.exceptions.UserProviderException;

import java.security.NoSuchAlgorithmException;

public class UserProvider {
    private static UserProvider INSTANCE;


    private Context context;
    private User currentUser;
    private String username;
    private int id;

    private UserProvider(Context context) {
        this.context = context;
        this.currentUser = null;
    }

    public static UserProvider getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new UserProvider(context);
        }
        return INSTANCE;
    }

    public User login(ILoginServiceRemote remote, String password) throws UserProviderException, NoSuchAlgorithmException, RemoteException, LoginException {

        DatabaseProvider connection = DatabaseProvider.getConnection(context);
        Cursor cursor = connection.query(DatabaseProvider.GET_USER_ID, username);

        id = -1;
        String salt = null;
        String passwordHash = null;
        String dbPasswordHash = null;

        if (cursor.moveToNext()) {
            id = cursor.getInt(0);
        }

        if (id == -1) {
            throw new UserProviderException("Couldn't find your username");
        }

        cursor = connection.query(DatabaseProvider.GET_SALT_AND_PASSWORDHASH_BY_ID, Integer.toString(id));

        if (cursor.moveToNext()) {
            salt = cursor.getString(0);
            dbPasswordHash = cursor.getString(1);
        }

        if (salt == null || dbPasswordHash == null) {
            throw new UserProviderException("Something went wrong");
        }

        passwordHash = Utils.getHashedString(password + Utils.getHashedString(salt));

        if (passwordHash == null) {
            throw new UserProviderException("Something went wrong");
        }

        if(remote != null) {
            boolean blocked = remote.isUserBlocked(id);

            if (blocked) {
                throw new LoginException("Sorry, but your user is blocked!", LoginException.BLOCKED);
            }

            boolean result = remote.login(id, passwordHash, dbPasswordHash);

            if (!result) {
                throw new LoginException("Your login credentials are wrong!", LoginException.WRONG);
            }
        }

        if (currentUser == null) {
            currentUser = new User(id, username, password, salt, passwordHash);
        }

        cursor.close();
        DatabaseProvider.dismiss();
        return currentUser;
    }

    public boolean userExists(String username) {
        Cursor cursor = DatabaseProvider.getConnection(context).query(DatabaseProvider.DOES_USER_EXISTS, username);

        int i = 0;
        if (cursor.moveToNext()) {
            i = cursor.getInt(0);
        }

        cursor.close();
        DatabaseProvider.dismiss();
        return i == 1;
    }

    public User createUser(String username, String password, String salt, boolean autoLogin) throws UserProviderException, NoSuchAlgorithmException, RemoteException, LoginException {
        if (userExists(username)) {
            throw new UserProviderException("Your username already exists");
        }

        String passwordHash = null;
        try {
            passwordHash = Utils.getHashedString(password + Utils.getHashedString(salt));
        } catch (NoSuchAlgorithmException e) {
            throw new UserProviderException("Something went wrong by creating the user!");
        }

        long id = DatabaseProvider.getConnection(context).insert(DatabaseProvider.CREATE_USER, username, passwordHash, salt);
        if (autoLogin) {
            setUsername(username);
            User user = login(null, password);
            return user;
        }

        if (currentUser == null) {
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

    public int getId() {
        return id;
    }
}
