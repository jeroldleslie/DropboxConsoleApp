/**
 * 
 */
package com.dropboxclient.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.HttpRequestor;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropboxclient.commands.ListCommand.Result;

/**
 * @author Peter Jerold Leslie
 *
 */
public class ListCommandTest {

  static ListFolderResult             expected;
  static FileMetadata                 expectedFileMetadata;
  static FolderMetadata               expectedFolderMetadata;
  static ListCommand                  listCommand;

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

  @Before
  public void setup() throws IOException {

    PrintStream out = new PrintStream(outContent);
    PrintStream error = new PrintStream(errContent);
    System.setOut(out);
    System.setErr(error);

    InputStream in = new ByteArrayInputStream("sd".getBytes());
    System.setIn(in);
    
    List<Metadata> entries = new ArrayList<>();
    expectedFileMetadata = new FileMetadata("banana.png", "id:eRsVsAya9YAAAAAAAAAAAQ", new Date(1456173312172L),
        new Date(1456173312172L), "89df885732c38", 12345L);
    entries.add(expectedFileMetadata);

    expectedFolderMetadata = new FolderMetadata("/fakepath", "id:eRsVsAya9YAAAAAAAAAAA1");
    entries.add(expectedFolderMetadata);

    expected = new ListFolderResult(entries,
        "AAEL4NJwGMzjKwpAIpzeV2qEv86iExc79keqHzcli5r4e4rdFSwAC9yLpXtdjryEB3g_U6s9c-oIkNvc_H9Z652G-vV_a4Mu56r1uNOxunMdWrpMxKXHJbF7fJs2vsMsdUcHdgVJHCGalrXBow9Ei5gvhv7OdBGR0xukeqmOqPJlBLM8Ac3DVnBUMRC6GGxopYo",
        false);
    
    listCommand = new ListCommand(mockDbxClientV2(), out);
  }

  @After
  public void tearDown() {
    expected = null;
    expectedFileMetadata = null;
    expectedFolderMetadata = null;
    listCommand = null;
    System.setOut(null);
    System.setErr(null);
  }

  @Test
  public void testListCommandWithCorrectArgs() throws Exception {
    String[] args = { "fakeToken", "/fakepath" };
    listCommand.parseAndBuild(listCommand, args);
    List<Result> results = (List<Result>) listCommand.execute();

    assertNotNull(results);
    assertEquals(results.size() - 1, expected.getEntries().size());
  }
  
  @Test
  public void testListCommandConsoleOutputWithCorrectArgs() throws Exception {
    String[] args = { "fakeToken", "/fakepath" };
    listCommand.parseAndBuild(listCommand, args);
    listCommand.print(listCommand.execute());

    assertEquals(outContent.toString().contains("List of file or folder for the given path in your dropbox"), true);
    assertEquals(outContent.toString().contains(expectedFileMetadata.getServerModified().toString()), true);
  }
  

  @Test
  public void testListCommandReturnedFile() throws Exception {
    String[] args = { "fakeToken", "/fakepath" };
    listCommand.parseAndBuild(listCommand, args);
    List<Result> results = (List<Result>) listCommand.execute();

    FileMetadata fileMetadata = null;
    for (Result result : results) {
      Metadata metadata = result.getMetadata();
      if (metadata instanceof FileMetadata) {
        fileMetadata = (FileMetadata) metadata;
        break;
      }
    }
    assertNotNull(fileMetadata);
    assertEquals(fileMetadata.getName(), expectedFileMetadata.getName());
  }

  @Test
  public void testListCommandReturnedFolder() throws Exception {
    String[] args = { "fakeToken", "/fakepath" };
    listCommand.parseAndBuild(listCommand, args);
    List<Result> results = (List<Result>) listCommand.execute();

    FolderMetadata folderMetadata = null;
    for (Result result : results) {
      Metadata metadata = result.getMetadata();
      if (metadata instanceof FolderMetadata) {
        folderMetadata = (FolderMetadata) metadata;
        break;
      }
    }
    assertNotNull(folderMetadata);
    assertEquals(folderMetadata.getName(), expectedFolderMetadata.getName());
  }

  @Test(expected = Exception.class)
  public void testListCommandWithInvalidArgs() throws Exception {
    String[] args = { "fakeToken" };
    listCommand.parseAndBuild(listCommand, args);
  }

  private static DbxClientV2 mockDbxClientV2() throws IOException {
    HttpRequestor mockRequestor = mock(HttpRequestor.class);
    DbxRequestConfig config = createRequestConfig().withAutoRetryEnabled(3).withHttpRequestor(mockRequestor)
        .withUserLocale(Locale.getDefault().toString()).build();

    DbxClientV2 client = new DbxClientV2(config, "fakeAccessToken");

    HttpRequestor.Uploader mockUploader = mockUploader();
    when(mockUploader.finish()).thenReturn(createSuccessResponse(expected.toString()))
        .thenReturn(createSuccessResponse(expectedFolderMetadata.toString()));
    when(mockRequestor.startPost(anyString(), anyHeaders())).thenReturn(mockUploader);
    return client;
  }

  private static DbxRequestConfig.Builder createRequestConfig() {
    return DbxRequestConfig.newBuilder("sdk-test");
  }

  private static HttpRequestor.Uploader mockUploader() {
    HttpRequestor.Uploader uploader = mock(HttpRequestor.Uploader.class);
    when(uploader.getBody()).thenAnswer(new Answer<OutputStream>() {
      @Override
      public OutputStream answer(InvocationOnMock invocation) {
        return new ByteArrayOutputStream();
      }
    });
    return uploader;
  }

  private static HttpRequestor.Response createSuccessResponse(String json) throws IOException {
    byte[] body = json.getBytes("UTF-8");
    return new HttpRequestor.Response(200, new ByteArrayInputStream(body),
        Collections.<String, List<String>>emptyMap());
  }

  private static Iterable<HttpRequestor.Header> anyHeaders() {
    return Matchers.<Iterable<HttpRequestor.Header>>any();
  }
}
