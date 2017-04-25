/**
 * 
 */
package com.dropboxclient.commands;

import static com.dropboxclient.util.Utils.indent;
import static com.dropboxclient.util.Utils.repeat;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dropbox.core.v2.DbxClientV2;
import com.dropboxclient.commands.annotations.Parameters;
import com.dropboxclient.shell.Console;

/**
 * @author Peter Jerold Leslie
 *
 */

@Parameters(commandName = Commands.HELP, commandDescription = "This command prints command line usage.")
public class HelpCommand extends AbtractCommandImpl {
  /**
   * log
   */
  private static final Logger LOG = Logger.getLogger(HelpCommand.class);

  /**
   * @param console
   */
  public HelpCommand(Console console) {
    super(HelpCommand.class);
    super.console = console;
    LOG.info("HelpCommand initiated using Console");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.dropboxclient.commands.AbstractCommand#usage(java.util.Map)
   */
  @Override
  public void usage(Map<String, Object> commands) throws IOException {
    LOG.info("printing usage for HelpCommand");
    console.println("");
    console.h1("Dropbox console client usage");
    console.print("Usage: <main class> [command] [command parameters] [options]");
    console.println("\n\nOptions:");
    console.print(repeat(" ", indent));
    console.print("--log");
    console.print(repeat(" ", indent));
    console.print("Prints debug logs in console");
    console.println("\nCommands:");
    for (Map.Entry<String, Object> entry : commands.entrySet()) {
      AbstractCommand command = (AbstractCommand) entry.getValue();
      try {
        console.print(command.usage(indent));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    LOG.info("HelpCommand completed succesfully");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.dropboxclient.commands.AbstractCommand#execute()
   */
  @Override
  public Object execute() throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.dropboxclient.commands.AbstractCommand#print(java.lang.Object)
   */
  @Override
  public void print(Object object) throws Exception {
    // TODO Auto-generated method stub

  }

}
