/**
 * 
 */
package com.dropboxclient.shell;

import static org.junit.Assert.assertEquals;
import static mocks.DropboxMock.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.AccountType;
import com.dropbox.core.v2.users.FullAccount;
import com.dropbox.core.v2.users.Name;

/**
 * @author Peter Jerold Leslie
 *
 */
public class InfoCommandShellTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  

  static Shell shell;
  static FullAccount                  expectedFullAccount;
  PrintStream out;
  PrintStream error;
  
  @Before
  public void setup() throws IOException, DbxException {
    out = new PrintStream(outContent);
    error = new PrintStream(errContent);
    System.setOut(out);
    System.setErr(error);
    
    expectedFullAccount = FullAccount.newBuilder("dbid:AAH4f99T0taONIb-OurWxbNQ6ywGRopQngc",
        new Name("Franz", "Ferdinand", "Franz", "Franz Ferdinand (Personal)"), "franz@dropbox.com", true, false, "en",
        "https://db.tt/ZITNuhtI", false, AccountType.BASIC).build();
    
  }
  
  @After
  public void tearDown() {
    shell = null;
    System.setOut(null);
    System.setErr(null);
  }
 
  @Test
  public void testConsoleOutputWithCorrectArgs() throws Exception {
    String[] args = { "info","fakeAccessToken" };
    shell = new Shell(null, mockDbxClientV2ForInfo(expectedFullAccount), out);
    shell.execute(args);

    assertEquals(outContent.toString().contains("Account information of your dropbox"), true);
    assertEquals(outContent.toString().contains(expectedFullAccount.getAccountId()), true);
  }
  
  @Test(expected = Exception.class)
  public void testConsoleOutputWithUnknownCommand() throws Exception {
    String[] args = { "infos", "fakeAccessToken" };
    shell = new Shell(null, mockDbxClientV2ForInfo(expectedFullAccount), out);
    shell.execute(args);
  }
  
  @Test(expected = Exception.class)
  public void testConsoleOutputWithInvalidArgs() throws Exception {
    String[] args = { "info"};
    shell = new Shell(null, mockDbxClientV2ForInfo(expectedFullAccount), out);
    shell.execute(args);
  }
  
}
