/**
 * 
 */
package com.dropboxclient.commands;

import java.io.PrintStream;

import org.apache.log4j.Logger;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;
import com.dropbox.core.v2.users.Name;
import com.dropboxclient.commands.annotations.Parameter;
import com.dropboxclient.commands.annotations.Parameters;
import com.dropboxclient.shell.Console;
import com.dropboxclient.util.TabularFormater;

/**
 * @author Peter Jerold Leslie
 *
 */
@Parameters(commandName = Commands.INFO, commandDescription = "This command retrieves information about Dropbox user's account.")
public class InfoCommand extends AbtractCommandImpl {
  /**
   * log
   */
  private static final Logger LOG = Logger.getLogger(InfoCommand.class);
  
  /**
   * Drobbox app access token
   */
  @Parameter(description = "the authorization code, which could be generated using auth command", mandatory = true, isToken = true)
  public String accessToken;

  /**
   * @param console
   */
  public InfoCommand(Console console) {
    super(InfoCommand.class);
    super.console = console;
    LOG.info("InfoCommand initiated using Console");
  }

  public InfoCommand(DbxClientV2 client, PrintStream out) {
    super(InfoCommand.class);
    this.console = new Console(out);
    this.client = client;
    LOG.info("InfoCommand initiated using DbxClientV2, PrintStream");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.dropboxclient.commands.AbtractCommand#execute()
   */
  @Override
  public FullAccount execute() throws Exception {
    LOG.info("start executing info command");
    // Get current account info
    FullAccount account = client.users().getCurrentAccount();
    LOG.info("got dropbox user account information");
    return account;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.dropboxclient.commands.AbstractCommand#print(java.lang.Object)
   */
  @Override
  public void print(Object object) throws Exception {
    LOG.info("printing info command result on console");
    FullAccount account = (FullAccount) object;
    TabularFormater formatter = new TabularFormater("", "", "");
    Name name = account.getName();
    formatter.addRow("User ID", ":", account.getAccountId());
    formatter.addRow("Display name", ":", name.getDisplayName());
    formatter.addRow("Name", ":", name.getGivenName() + " " + name.getSurname() + " (" + name.getFamiliarName() + ")");
    formatter.addRow("Email", ":",
        account.getEmail() + " (" + (account.getEmailVerified() ? "verified" : "not verified") + ")");
    formatter.addRow("Country", ":", account.getCountry());
    formatter.addRow("Referral link", ":", account.getReferralLink());
    console.h1("Account information of your dropbox");
    console.println(formatter.getFormattedText());
    LOG.info("printing info command result on console completed");
  }

}
