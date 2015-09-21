package core;

import java.util.ArrayList;
import java.util.List;

public class User {

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
