
package com.dropboxclient.commands;

import java.io.IOException;
import java.util.Map;

import com.dropboxclient.shell.Console;

/**
 * @author Peter Jerold Leslie 
 */
/**
 * This class contains all abrstact methods and variables that should implement
 * in every command
 */
/**
 * @author Peter Jerold Leslie
 *
 */
public abstract class AbstractCommand {

  /**
   * 
   */
  protected Console           console;

  /**
   * @return
   * @throws Exception
   */
  public abstract Object execute() throws Exception;

  /**
   * @param object
   * @throws Exception
   */
  public abstract void print(Object object) throws Exception;

  /**
   * @param object
   * @param args
   * @throws Exception
   */
  public abstract void parseAndBuild(Object object, String[] args) throws Exception;

  /**
   * @param startIndent
   * @return
   */
  public abstract String usage(int startIndent);

  /**
   * @param commands
   * @throws IOException
   */
  public abstract void usage(Map<String, Object> commands) throws IOException;
}
