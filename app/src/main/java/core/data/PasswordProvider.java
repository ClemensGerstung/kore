package core.data;

import android.content.Context;
import android.support.annotation.Nullable;
import core.DatabaseProvider;
import core.exceptions.PasswordProviderException;

import java.util.*;

public class PasswordProvider {
    private static PasswordProvider Instance;

    private Context context;
    private List<Password> passwords;


    PasswordProvider(Context context) {
        this.context = context;
        this.passwords = new ArrayList<>();
    }

    public static PasswordProvider getInstance(Context context) {
        if(Instance == null)
            Instance = new PasswordProvider(context);
        return Instance;
    }

    public void add(Password password) {
        if(password.getPosition() == Integer.MIN_VALUE)
            password.setPosition(passwords.size() + 1);

        passwords.add(password);
    }

    public void set(Password password) {
        int index = passwords.indexOf(password);
        passwords.set(index, password);
    }

    public void remove(Password password) {
        passwords.remove(password);
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

    public boolean contains(Password p) {
        return passwords.contains(p);
    }

    void logoutComplete() {
        simpleLogout();
        context = null;
    }

    void simpleLogout() {
        for (Password p : passwords) {
            p.logout();
        }
        passwords.clear();
    }

    List<Password> getPasswords() {
        return passwords;
    }

    public void order(int which) {
        if (which == 0) {   // order by username ascending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    int compareTo = lhs.getUsername().compareTo(rhs.getUsername());
                    if(compareTo != 0)
                        lhs.swapPositionWith(rhs);

                    return compareTo;
                }
            });
        } else if (which == 1) {    // order by username descending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    int compareTo = ~lhs.getUsername().compareTo(rhs.getUsername());
                    if(compareTo != 0)
                        lhs.swapPositionWith(rhs);

                    return compareTo;
                }
            });
        } else if (which == 2) {   // order by password ascending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    int compareTo = lhs.getFirstItem().compareTo(rhs.getFirstItem());
                    if(compareTo != 0)
                        lhs.swapPositionWith(rhs);

                    return compareTo;
                }
            });
        } else if (which == 3) {    // order by password descending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    int compareTo = ~lhs.getFirstItem().compareTo(rhs.getFirstItem());
                    if(compareTo != 0)
                        lhs.swapPositionWith(rhs);

                    return compareTo;
                }
            });
        } else if (which == 4) {   // order by program ascending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    int compareTo = lhs.getProgram().compareTo(rhs.getProgram());
                    if(compareTo != 0)
                        lhs.swapPositionWith(rhs);

                    return compareTo;
                }
            });
        } else if (which == 5) {    // order by program descending
            Collections.sort(passwords, new Comparator<Password>() {
                @Override
                public int compare(Password lhs, Password rhs) {
                    int compareTo = ~lhs.getProgram().compareTo(rhs.getProgram());
                    if(compareTo != 0)
                        lhs.swapPositionWith(rhs);

                    return compareTo;
                }
            });
        }


    }
}
