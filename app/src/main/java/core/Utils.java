package core;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

  /**
   * All available characters for passwords
   */
  public static String AVAILABLE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890-_.:,;+*?!%&/";

  /**
   * The regex string for safe passwords
   * (min 8 chars, upper, lower case letters, digits and some special chars)
   */
  public static String REGEX_PASSWORD_SAFETY = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[-_.:,;+*?!%&/]).{8,})";

  /**
   * Hashes the data with the SHA1 algorithm
   *
   * @param data to notify hash from
   * @return the generated hash
   * @throws NoSuchAlgorithmException
   */
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

  public static boolean isSafe(String password) {
    return password.matches(REGEX_PASSWORD_SAFETY);
  }

  public static Date getDateFromString(String date) throws ParseException {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    return dateFormat.parse(date);

  }

  public static String getStringFromDate(Date date) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    return dateFormat.format(date);
  }

  public static String getToday() {
    Date date = new Date();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    return dateFormat.format(date);
  }

  public static boolean isRooted() {
    String binaryName = "su";

    boolean found = false;

    String[] places = {
        "/sbin/",
        "/system/bin/",
        "/system/xbin/",
        "/data/local/xbin/",
        "/data/local/bin/",
        "/system/sd/xbin/",
        "/system/bin/failsafe/",
        "/data/local/"
    };

    for (String where : places) {
      if (new File(where + binaryName).exists()) {
        found = true;
        break;
      }
    }

    return found;
  }

  public static boolean isDeviceOnline(Context context) {
    ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    // TODO: check WIFI
    return (networkInfo != null && networkInfo.isConnected());
  }

}
