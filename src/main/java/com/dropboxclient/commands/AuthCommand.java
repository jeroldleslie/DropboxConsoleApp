/**
 * 
 */
package com.dropboxclient.commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropboxclient.commands.annotations.Parameter;
import com.dropboxclient.commands.annotations.Parameters;
import com.dropboxclient.shell.Console;

/**
 * @author Peter Jerold Leslie
 *
 */
@Parameters(commandName = Commands.AUTH, commandDescription = "This command authenticates Dropbox user's account. ")
public class AuthCommand extends AbtractCommandImpl {
  /**
   * log
   */
  private static final Logger LOG = Logger.getLogger(AuthCommand.class);
  /**
   * Dropbox application key
   */
  @Parameter(description = "the Dropbox application key ", mandatory = true)
  public String               appKey;
  /**
   * Dropbox application secret
   */
  @Parameter(description = "the Dropbox application secret code", mandatory = true)
  public String               appSecret;
  /**
   * Dropbox web authentication finish
   */
  private DbxAuthFinish       authFinish;
  /**
   * @param console
   */
  public AuthCommand(Console console) {
    super(AuthCommand.class);
    super.console = console;
    LOG.info("AuthCommand initiated using Console");
  }

  /**
   * @param authFinish
   * @param out
   */
  public AuthCommand(DbxAuthFinish authFinish, PrintStream out) {
    super(AuthCommand.class);
    this.console = new Console(out);
    this.authFinish = authFinish;
    LOG.info("AuthCommand initiated using DbxAuthFinish, PrintStream");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.dropboxclient.commands.AbstractCommand#execute()
   */
  public String execute() throws Exception {
    LOG.info("start executing command");
    DbxAppInfo appInfo = new DbxAppInfo(appKey, appSecret);
    DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
    if (authFinish == null) {
      // Have the user sign in and authorize your app.
      LOG.info("Dropbox client web authentication started");
      String authorizeUrl = webAuth.start();
      console.h1("Dropbox Authentication - Please follow the instructions below to generate access token");
      console.println("1. Go to: " + authorizeUrl);
      console.println("2. Click \"Allow\" (you might have to log in first)");
      console.println("3. Copy the authorization code.");
      LOG.info("Dropbox client web authentication waiting for key");
      String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();

      // This will fail if the user enters an invalid authorization code.
      try {
        authFinish = webAuth.finish(code);
        LOG.info("Dropbox client web authentication completed");
      } catch (DbxException e) {
        LOG.error(e);
        System.out.println(e.getMessage());
      }
    }
    LOG.info("command execute completed");
    return authFinish.getAccessToken();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.dropboxclient.commands.AbstractCommand#print(java.lang.Object)
   */
  @Override
  public void print(Object object) throws Exception {
    LOG.info("printing auth result on console");
    console.println("");
    console.h2(
        "Access token for Dropbox application has been successfully generated. Please use below token for other commands");
    console.println("");
    console.println(object.toString());
    LOG.info("Auth command completed successfully");
  }

}
