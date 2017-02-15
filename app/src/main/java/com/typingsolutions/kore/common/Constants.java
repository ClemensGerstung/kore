package com.typingsolutions.kore.common;

public final class Constants {
  /**
   * All available characters for passwords
   */
  public static String AVAILABLE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890-_.:,;+*?!%&/";

  /**
   * The regex string for safe passwords
   * (min 8 chars, upper, lower case letters, digits and some special chars)
   */
  public static String REGEX_PASSWORD_SAFETY = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[-_.:,;+*?!%&/]).{8,})";
}
