package core;

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
   * @param data to create hash from
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

  public static int copyFile(File source, File target, Flag flag) {
    int size = 0;

    try {
      size = copyFile(new FileInputStream(source), new FileOutputStream(target), flag);
    } catch (Exception e) {
      return -1;
    }

    return size;
  }

  public static int copyFile(FileDescriptor source, File target, Flag flag) {
    int size = 0;

    try {
      size = copyFile(new FileInputStream(source), new FileOutputStream(target), flag);
    } catch (Exception e) {
      return -1;
    }

    return size;
  }

  public static int copyFile(File source, FileDescriptor target, Flag flag) {
    int size = 0;

    try {
      size = copyFile(new FileInputStream(source), new FileOutputStream(target), flag);
    } catch (Exception e) {
      return -1;
    }

    return size;
  }

  private static int copyFile(FileInputStream source, FileOutputStream target, Flag flag) {
    //TODO: check hash?
    int size = 0;
    byte[] buffer = new byte[1024];
    int length;

    try {
      while ((length = source.read(buffer)) > 0) {
        target.write(buffer, 0, length);
        size += length;
      }

      source.close();
      target.close();
    } catch (IOException e) {
      return -1;
    }

    return size;
  }

  public enum Flag {
    Backup, Restore
  }
}
