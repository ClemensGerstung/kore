package core;

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


  public static String AVAILABLE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890-_.:,;+*?!%&/";

  public static String REGEX_PASSWORD_SAFETY = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[-_.:,;+*?!%&/]).{8,})";

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

  public static Date getDateFromString(String date) throws ParseException {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Date dateObj = dateFormat.parse(date);
    return dateObj;
  }

  public static boolean isRooted() {
    try {
      Process p = Runtime.getRuntime().exec("su");
      p.waitFor();
      return p.exitValue() != 255;
    } catch (Exception e) {
      return false;
    }
  }
}
