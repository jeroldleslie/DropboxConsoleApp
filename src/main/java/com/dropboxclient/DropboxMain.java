/**
 * 
 */
package com.dropboxclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.dropboxclient.shell.Shell;

/**
 * @author Peter Jerold Leslie
 *
 */
public class DropboxMain {
  private static final Logger LOG = Logger.getLogger(DropboxMain.class);

  /**
   * @param args
   *          Command line arguments for dropbox console application 
   *          Usage:
   *          <main class> [command] [command parameters] [options]
   * 
   * @throws Exception
   *           Throws exception if any eroor occurs
   */
  public static void main(String[] args) throws Exception {
    boolean turnOffLogging = true;
    for (String arg : args) {
      if (arg.trim().equals("--log")) {
        turnOffLogging = false;
        List<String> list = new ArrayList<String>(Arrays.asList(args));
        list.remove(arg);
        args = list.toArray(new String[0]);
        break;
      }
      if (arg.trim().contains("--")) {
        List<String> list = new ArrayList<String>(Arrays.asList(args));
        list.remove(arg);
        args = list.toArray(new String[0]);
      }
    }
    if (turnOffLogging) {
      Logger.getRootLogger().setLevel(Level.OFF);
    }
    LOG.info("Dropbox console application started");
    Shell shell = new Shell();
    shell.execute(args);
  }

}
