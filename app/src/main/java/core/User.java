package core;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {

    private int id;
    private String name;
    private String plainPassword;
    private String passwordHash;
    private String salt;
    private final List<Password> passwords;

    public User(int id, String name, String plainPassword, String salt, String passwordHash) {
        this.id = id;
        this.name = name;
        this.plainPassword = plainPassword;
        this.salt = salt;
        this.passwordHash = passwordHash;
        this.passwords = new ArrayList<Password>();
    }

    /**
     * Constructor for IPC.<br>
     * Only set the passwordHash so no plain password will be sent.
     *
     * @param id id of user
     * @param name entered username
     * @param passwordHash hash of the entered password
     * */
    public User(int id, String name, String salt, String passwordHash) {
        this(id, name, "", salt, passwordHash);
    }

    public static final Parcelable.Creator<User> CREATOR = new ClassLoaderCreator<User>() {
        @Override
        public User createFromParcel(Parcel source, ClassLoader loader) {
            int id = source.readInt();
            String name = source.readString();
            String passwordHash = source.readString();
            String salt = source.readString();

            return new User(id, name, salt, passwordHash);
        }

        @Override
        public User createFromParcel(Parcel source) {
            return createFromParcel(source, null);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(passwordHash);
        dest.writeString(salt);
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<Password> getPasswords() {
        return passwords;
    }
}
