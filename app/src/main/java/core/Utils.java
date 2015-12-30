package core;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.*;
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
    return dateFormat.parse(date);
  }

  public static boolean isRooted() {
    try {
      File f = new File("/data");
      return f.mkdir();
    } catch (Exception e) {
      return false;
    }
  }

  public static int copyFile(File source, File target) {
    int size = 0;

    try {
      size = copyFile(new FileInputStream(source), new FileOutputStream(target));
    } catch (Exception e) {
      return -1;
    }

    return size;
  }

  public static int copyFile(FileDescriptor source, File target) {
    int size = 0;

    try {
      size = copyFile(new FileInputStream(source), new FileOutputStream(target));
    } catch (Exception e) {
      return -1;
    }

    return size;
  }

  public static int copyFile(File source, FileDescriptor target) {
    int size = 0;

    try {
      size = copyFile(new FileInputStream(source), new FileOutputStream(target));
    } catch (Exception e) {
      return -1;
    }

    return size;
  }

  private static int copyFile(FileInputStream source, FileOutputStream target) {
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
}
