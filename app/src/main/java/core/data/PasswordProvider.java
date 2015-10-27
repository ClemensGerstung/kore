package core.data;

import android.content.Context;
import android.support.annotation.Nullable;
import core.DatabaseProvider;
import core.exceptions.PasswordProviderException;

import java.util.*;

class PasswordProvider {
    private Context context;
    private int userId;
    private List<Password> passwords;


    PasswordProvider(Context context, int userId) {
        this.context = context;
        this.userId = userId;
        this.passwords = new ArrayList<>();
    }

    public void add(Password password) {
        if(password.getPosition() == Integer.MIN_VALUE)
            password.setPosition(passwords.size());

        passwords.add(password);
    }

    private int insertPasswordHistoryItem(String value, String date, int passwordId) throws PasswordProviderException {
        int historyId = -1;
//        DatabaseProvider connection = DatabaseProvider.getConnection(context);
//
//        historyId = (int) connection.insert(DatabaseProvider.INSERT_HISTORY_FOR_PASSWORD, value, date, Integer.toString(passwordId));
//
//        if (historyId == -1) {
//            throw new PasswordProviderException("Couldn't insert your password");
//        }

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
//        if (username != null && program != null) {
//            returnedValue = provider.update(DatabaseProvider.UPDATE_USERNAME_AND_PASSWORD, username, program, Integer.toString(id));
//        } else if (username != null && program == null) {
//            returnedValue = provider.update(DatabaseProvider.UPDATE_USERNAME, username, Integer.toString(id));
//        } else if (program != null && username == null) {
//            returnedValue = provider.update(DatabaseProvider.UPDATE_PROGRAM, program, Integer.toString(id));
//        } else {
//            throw new PasswordProviderException("Not supported operation: either username or password has to be something");
//        }

        if (returnedValue == -1) {
            throw new PasswordProviderException("There was an error by updating this password");
        }

        provider.close();
    }

    public boolean contains(Password p) {
        return passwords.contains(p);
    }

    void logoutComplete() {
        for (Password p : passwords) {
            p.logout();
        }
        passwords.clear();
        context = null;
        userId = -1;
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
                    return lhs.getFirstItem().compareTo(rhs.getFirstItem());
                }
            });
        } else if (which == 3) {    // order by password descending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    return ~lhs.getFirstItem().compareTo(rhs.getFirstItem());
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
