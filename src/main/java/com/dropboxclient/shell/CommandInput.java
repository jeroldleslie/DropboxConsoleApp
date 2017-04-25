/**
 * 
 */
package com.dropboxclient.shell;

import org.apache.log4j.Logger;

/**
 * @author Peter Jerold Leslie
 *
 */
public class CommandInput {
  /**
   * log
   */
  private static final Logger LOG = Logger.getLogger(CommandInput.class);
  
  /**
   * command line args as string
   */
  private String   commandLine;
  /**
   * command, example: list
   */
  private String   command;
  /**
   * remaining arguments as string array
   */
  private String[] remainArgs;

  /**
   * @param args
   */
  public CommandInput(String[] args) {
    command = args[0];
    args = shift(args);
    if (args == null || args.length == 0)
      return;
    remainArgs = args;
    LOG.info("CommandInput initiated using String[] args");
  }

  /**
   * @return commandLine
   */
  public String getCommandLine() {
    return this.commandLine;
  }

  /**
   * @return command
   */
  public String getCommand() {
    return this.command;
  }

  /**
   * @param command
   */
  public void setCommand(String command) {
    this.command = command;
  }

  /**
   * @return remainArgs
   */
  public String[] getRemainArgs() {
    return this.remainArgs;
  }

  /**
   * @param array
   * @return array
   */
  static public String[] shift(String[] array) {
    if (array == null || array.length == 0)
      return null;
    String[] newArray = new String[array.length - 1];
    System.arraycopy(array, 1, newArray, 0, newArray.length);
    return newArray;
  }

}
