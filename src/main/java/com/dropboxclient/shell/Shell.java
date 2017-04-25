/**
 * 
 */
package com.dropboxclient.shell;

import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.v2.DbxClientV2;
import com.dropboxclient.commands.AbstractCommand;
import com.dropboxclient.commands.AuthCommand;
import com.dropboxclient.commands.Commands;
import com.dropboxclient.commands.HelpCommand;
import com.dropboxclient.commands.InfoCommand;
import com.dropboxclient.commands.ListCommand;

/**
 * @author Peter Jerold Leslie
 *
 */
public class Shell {
  /**
   * log
   */
  private static final Logger LOG = Logger.getLogger(Shell.class);
  /**
   * Console for console output
   */
  protected Console             console;
  /**
   * map to hold command objects
   */
  protected Map<String, Object> commands = new TreeMap<String, Object>();
  /**
   * helpcommand object
   */
  private HelpCommand           helpCommand;
  

  /**
   * 
   */
  public Shell() {
    LOG.info("Initiating Shell");
    console = new Console();
    helpCommand = new HelpCommand(console);
    LOG.info("Adding commands");
    add(Commands.AUTH, new AuthCommand(console));
    add(Commands.LIST, new ListCommand(console));
    add(Commands.INFO, new InfoCommand(console));
    add(Commands.HELP, helpCommand);
    LOG.info("Shell initiated");
  }
  
  /**
   * @param authFinish
   * @param client
   * @param out
   */
  public Shell(DbxAuthFinish authFinish,DbxClientV2 client,PrintStream out) {
    LOG.info("Initiating Shell");
    console = new Console(out);
    helpCommand = new HelpCommand(console);
    LOG.info("Adding commands");
    add(Commands.AUTH, new AuthCommand(authFinish,out));
    add(Commands.LIST, new ListCommand(client,out));
    add(Commands.INFO, new InfoCommand(client,out));
    add(Commands.HELP, helpCommand);
    LOG.info("Shell initiated using DbxAuthFinish, DbxClientV2, PrintStream");
  }

  /**
   * @param name
   * @param command
   */
  public void add(String name, Object command) {
    LOG.info("Adding " +name+ " command");
    commands.put(name, command);
  }

  /**
   * @param args
   * @throws Exception
   */
  public void execute(String[] args) throws Exception {
    LOG.info("Executing shell");
    CommandInput cmdInput = new CommandInput(args);
    if (commands.get(cmdInput.getCommand()) == null) {
      LOG.error("Unknown command");
      console.print("Error : Unknown command. Please do read dropbox console client usage");
      helpCommand.usage(commands);
      throw new Exception("Unknown command");
    }

    Object object = commands.get(cmdInput.getCommand());
    AbstractCommand command = (AbstractCommand) object;

    if (cmdInput.getCommand().equals(Commands.HELP)) {
      LOG.info("printing help");
      command.usage(commands);
    }
    
    try {
      LOG.info("parse and build started from shell");
      command.parseAndBuild(object, cmdInput.getRemainArgs());
    } catch (Exception e) {
      LOG.error(e);
      console.print("Error : ");
      console.print(e.getMessage());
      console.println("\nCommand Usage:");
      console.println(command.usage(2));
      throw e;
    }
    
    try {
      LOG.info("executing command from shell");
      Object obj = command.execute();
      LOG.info("command execution completed from shell");
      command.print(obj);
    } catch (Exception e) {
      LOG.error(e);
      e.printStackTrace();
    }
    
    LOG.info("Dropbox console client executed successfully");
  }
}
