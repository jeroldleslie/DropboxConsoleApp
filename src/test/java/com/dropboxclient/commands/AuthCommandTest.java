/**
 * 
 */
package com.dropboxclient.commands;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;

/**
 * @author Peter Jerold Leslie
 *
 */
public class AuthCommandTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  static AuthCommand authCommand;
  
  @Before
  public void setup() throws IOException, DbxException {
    PrintStream out = new PrintStream(outContent);
    PrintStream error = new PrintStream(errContent);
    System.setOut(out);
    System.setErr(error);
    authCommand = new AuthCommand(new DbxAuthFinish("fakeaccesstoken", "fackuserid", "fackuserstae"), System.out);
  }
  
  @After
  public void tearDown() {
    authCommand = null;
    System.setOut(null);
    System.setErr(null);
  }
  
  @Test
  public void testAuthCommandWithCorrectArgs() throws Exception {
    String[] args = { "fake_appkey", "fake_secret" };
    authCommand.parseAndBuild(authCommand, args);
    
    String authFinish = authCommand.execute();
    assertNotNull(authFinish);
    assertEquals("fakeaccesstoken", authFinish);
  }
  
  @Test(expected = Exception.class)
  public void testAuthCommandWithInvalidArgs() throws Exception {
    String[] args = { "fake_appkey"};
    authCommand.parseAndBuild(authCommand, args);
  }
  
  @Test
  public void testAuthCommandConsoleOutputWithCorrectArgs() throws Exception {
    String[] args = { "fake_appkey", "fake_secret" };
    authCommand.parseAndBuild(authCommand, args);
    authCommand.print(authCommand.execute());
    
    assertEquals(outContent.toString().contains("Access token for Dropbox application has been successfully generated. Please use below token for other commands"), true);
    assertEquals(outContent.toString().contains("fakeaccesstoken"), true);
  }
}
