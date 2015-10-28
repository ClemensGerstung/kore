package core.data;

import android.content.Context;
import android.database.Cursor;
import com.typingsolutions.passwordmanager.ILoginServiceRemote;
import core.AesProvider;
import core.DatabaseProvider;
import core.Utils;
import core.exceptions.LoginException;
import core.exceptions.UserProviderException;

import java.security.NoSuchAlgorithmException;

public class UserProvider {
    private static UserProvider INSTANCE;

    private PasswordProvider passwordProvider;
    private Context context;
    private User currentUser;
    private String username;
    private int id;

    private UserProviderPasswordActionListener passwordActionListener;
    private UserProviderActionListener userProviderActionListener;

    private UserProvider(Context context) {
        this.context = context;
        this.currentUser = null;
        this.id = -1;
        this.username = null;
        this.passwordProvider = new PasswordProvider(context);
    }

    public static UserProvider getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new UserProvider(context);
        }
        return INSTANCE;
    }

    public User login(ILoginServiceRemote remote, String password, boolean safeLogin) throws Exception {
        if (username == null || id == -1)
            throw new UserProviderException("No username or id set");

        DatabaseProvider connection = DatabaseProvider.getConnection(context);

        String salt = null;
        String passwordHash = null;
        String dbPasswordHash = null;

        Cursor cursor = connection.query(DatabaseProvider.GET_SALT_AND_PASSWORDHASH_BY_ID, Integer.toString(id));

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

        if (remote != null) {
            boolean blocked = remote.isUserBlocked(id);

            if (blocked) {
                // at least you will never get in here because you're not able to click login
                throw new LoginException("Sorry, but your user is blocked!", LoginException.BLOCKED);
            }

            boolean result = remote.login(id, passwordHash, dbPasswordHash);

            if (!result) {
                throw new LoginException("Your login credentials are wrong!", LoginException.WRONG);
            }
        }

        if (currentUser == null) {
            currentUser = new User(id, username, password, salt, passwordHash);
            cursor = connection.query(DatabaseProvider.GET_PASSWORDIDS_FROM_USER, Integer.toString(id));
            if (cursor.moveToNext()) {
                String ids = AesProvider.decrypt(cursor.getString(0), password);
                currentUser.setPasswordIdsFromJson(ids);
            }
            currentUser.isSafeLogin(safeLogin);

            if (userProviderActionListener != null)
                userProviderActionListener.onLogin();
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

    public User createUser(String username, String password, String salt, boolean autoLogin) throws Exception {
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

        if (userProviderActionListener != null)
            userProviderActionListener.onUserCreated(id);

        if (autoLogin) {
            setUsername(username);
            User user = login(null, password, false);
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

    public void setUsername(String username) throws UserProviderException {
        this.username = username;
        DatabaseProvider connection = DatabaseProvider.getConnection(context);
        Cursor cursor = connection.query(DatabaseProvider.GET_USER_ID, username);
        id = -1;

        if (cursor.moveToNext()) {
            id = cursor.getInt(0);
        }

        if (id == -1) {
            this.username = null;
            throw new UserProviderException("Unknown username");
        }

        cursor.close();
        connection.close();

        if (userProviderActionListener != null)
            userProviderActionListener.onUsernameSet();
    }

    public int getId() {
        return id;
    }

    private void logoutComplete() {
        passwordProvider.logoutComplete();
        currentUser.logout();
        currentUser = null;
        username = null;
        id = -1;

        if (userProviderActionListener != null)
            userProviderActionListener.onLogout();
    }

    public boolean isSafe() {
        return currentUser.isSafeLogin();
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
        if (!password.hasId())
            return;

        passwordProvider.add(password);
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

    public void editPassword(int id, String program, String username) {

    }

    public void editPassword(Password password) {
        passwordProvider.set(password);

        if (passwordActionListener != null)
            passwordActionListener.onPasswordEdited(password, password.getFirstHistoryItem());
    }

    public void removePassword(Password password) {
        passwordProvider.remove(password);

        if (passwordActionListener != null)
            passwordActionListener.onPasswordRemoved(password);
    }

    public Password getPasswordById(int id) {
        return passwordProvider.getById(id);
    }

    public void setPasswordActionListener(UserProviderPasswordActionListener passwordActionListener) {
        this.passwordActionListener = passwordActionListener;
    }

    public void setUserProviderActionListener(UserProviderActionListener userProviderActionListener) {
        this.userProviderActionListener = userProviderActionListener;
    }

    public static void logout() {
        INSTANCE.logoutComplete();
        INSTANCE = null;
    }

    public static String decrypt(String data) throws Exception {
        if (INSTANCE == null || INSTANCE.currentUser == null)
            throw new UserProviderException("Cannot decrypt because there was an error");
        return AesProvider.decrypt(data, INSTANCE.currentUser.plainPassword);
    }

    public interface UserProviderPasswordActionListener {
        void onPasswordAdded(Password password);

        void onPasswordRemoved(Password password);

        void onPasswordEdited(Password password, PasswordHistory history);
    }

    public interface UserProviderActionListener {
        void onLogin();

        void onLogout();

        void onUsernameSet();

        void onUserCreated(long id);
    }
}
