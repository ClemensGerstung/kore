package core;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {


    static String AVAILABLE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890-_.:,;+*?!%&/";

    static String REGEX_PASSWORD_SAFETY = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[-_.:,;+*?!%&/]).{8,})";

    /**
     * Retrieves the net.hostname system property
     *
     * @param defValue the value to be returned if the hostname could
     *                 not be resolved
     */
    static String getHostName(String defValue) {
        try {
            Method getString = Build.class.getDeclaredMethod("getString", String.class);
            getString.setAccessible(true);
            return getString.invoke(null, "net.hostname").toString();
        } catch (Exception ex) {
            return defValue;
        }
    }

    public static String getHashedHostName() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String hostName = getHostName("android-1234567890");
        return getHashedString(hostName);
    }

    public static String getHashedString(String data) throws NoSuchAlgorithmException {
        byte[] key = data.getBytes();
        MessageDigest sha = MessageDigest.getInstance("sha1");
        byte[] hash = sha.digest(key);

        String result = "";
        for (byte b : hash) {
            result += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static String getSalt() {
        String salt = "";
        for (int i = 0; i < 15; i++) {
            int rnd = (int) (Math.random() * AVAILABLE_CHARACTERS.length());
            salt += AVAILABLE_CHARACTERS.toCharArray()[rnd];
        }
        return salt;
    }

    public static boolean isSafe(String password) {
        return password.matches(REGEX_PASSWORD_SAFETY);
    }

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDateAsSimpleString(Date date) {
        DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static Date getDateFromString(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.parse(date);
    }
}
