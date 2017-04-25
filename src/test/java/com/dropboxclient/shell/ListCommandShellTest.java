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
public class ListCommandShellTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  
  static ListFolderResult             expectedFolderResult;
  static FileMetadata                 expectedFileMetadata;
  static FolderMetadata               expectedFolderMetadata;
  static Shell shell;
  PrintStream out;
  PrintStream error;
  
  @Before
  public void setup() throws IOException, DbxException {
    out = new PrintStream(outContent);
    error = new PrintStream(errContent);
    System.setOut(out);
    System.setErr(error);
    
    List<Metadata> entries = new ArrayList<>();
    expectedFileMetadata = new FileMetadata("banana.png", "id:eRsVsAya9YAAAAAAAAAAAQ", new Date(1456173312172L),
        new Date(1456173312172L), "89df885732c38", 12345L);
    entries.add(expectedFileMetadata);

    expectedFolderMetadata = new FolderMetadata("/fakepath", "id:eRsVsAya9YAAAAAAAAAAA1");
    entries.add(expectedFolderMetadata);

    expectedFolderResult = new ListFolderResult(entries,
        "AAEL4NJwGMzjKwpAIpzeV2qEv86iExc79keqHzcli5r4e4rdFSwAC9yLpXtdjryEB3g_U6s9c-oIkNvc_H9Z652G-vV_a4Mu56r1uNOxunMdWrpMxKXHJbF7fJs2vsMsdUcHdgVJHCGalrXBow9Ei5gvhv7OdBGR0xukeqmOqPJlBLM8Ac3DVnBUMRC6GGxopYo",
        false);
  }
  
  @After
  public void tearDown() {
    shell = null;
    System.setOut(null);
    System.setErr(null);
  }
 
  @Test
  public void testConsoleOutputWithCorrectArgs() throws Exception {
    String[] args = { "list", "fakeToken", "/fakepath" };
    shell = new Shell(null, mockDbxClientV2ForList(expectedFolderResult,expectedFolderMetadata), out);
    shell.execute(args);

    assertEquals(outContent.toString().contains("List of file or folder for the given path in your dropbox"), true);
    assertEquals(outContent.toString().contains(expectedFileMetadata.getServerModified().toString()), true);
  }
  
  @Test(expected = Exception.class)
  public void testConsoleOutputWithUnknownCommand() throws Exception {
    String[] args = { "lists", "fakeToken", "/fakepath" };
    shell = new Shell(null, mockDbxClientV2ForList(expectedFolderResult,expectedFolderMetadata), out);
    shell.execute(args);
  }
  
  @Test(expected = Exception.class)
  public void testConsoleOutputWithInvalidArgs() throws Exception {
    String[] args = { "list", "fakeToken"};
    shell = new Shell(null, mockDbxClientV2ForList(expectedFolderResult,expectedFolderMetadata), out);
    shell.execute(args);
  }
  
}
