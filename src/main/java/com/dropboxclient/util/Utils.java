/**
 * 
 */
package com.dropboxclient.util;

import java.text.DecimalFormat;

/**
 * @author Peter Jerold Leslie
 *
 */
public final class Utils {
  public static int indent = 2;

  /**
   * @param str
   * @param count
   * @return
   */
  public static String repeat(String str, int count) {
    return new String(new char[count]).replace("\0", str);
  }

  /**
   * @param size
   * @return
   */
  public static String readableFileSize(long size) {
    if (size <= 0)
      return "0";
    final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
  }
}
