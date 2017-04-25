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
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
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
import com.dropbox.core.v2.users.AccountType;
import com.dropbox.core.v2.users.FullAccount;
import com.dropbox.core.v2.users.Name;

/**
 * @author Peter Jerold Leslie
 *
 */
public class InfoCommandTest {

  static FullAccount                  expected;
  static InfoCommand                  infoCommand;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

  @Before
  public void setup() throws IOException {
    PrintStream out = new PrintStream(outContent);
    PrintStream error =new PrintStream(errContent);
    System.setOut(out);
    System.setErr(error);
    
    expected = FullAccount.newBuilder("dbid:AAH4f99T0taONIb-OurWxbNQ6ywGRopQngc",
        new Name("Franz", "Ferdinand", "Franz", "Franz Ferdinand (Personal)"), "franz@dropbox.com", true, false, "en",
        "https://db.tt/ZITNuhtI", false, AccountType.BASIC).build();
    infoCommand = new InfoCommand(mockDbxClientV2(),out);
  }

  @After
  public void tearDown() {
    expected = null;
    infoCommand = null;
    System.setOut(null);
    System.setErr(null);
  }

  @Test
  public void testAccountInfoWithCorrectArgs() throws Exception {
    String[] args = { "fakeAccessToken" };
    infoCommand.parseAndBuild(infoCommand, args);
    FullAccount actual = (FullAccount) infoCommand.execute();

    assertNotNull(actual);
    assertNotNull(actual.getName());
    assertNotNull(actual.getAccountId());
    assertEquals(actual.getName(), expected.getName());
  }

  @Test
  public void testAccountCommandConsoleOutputWithCorrectArgs() throws Exception {
    String[] args = { "fakeAccessToken" };
    infoCommand.parseAndBuild(infoCommand, args);
    infoCommand.print(infoCommand.execute());
    assertEquals(outContent.toString().contains("Account information of your dropbox"), true);
    assertEquals(outContent.toString().contains(expected.getAccountId()), true);
  }
  
  @Test(expected = Exception.class)
  public void testAccountInfoWithInvalidArgs() throws Exception {
    String[] args = {};
    infoCommand.parseAndBuild(infoCommand, args);
  }
  

  private static DbxClientV2 mockDbxClientV2() throws IOException {
    HttpRequestor mockRequestor = mock(HttpRequestor.class);
    DbxRequestConfig config = createRequestConfig().withAutoRetryEnabled(3).withHttpRequestor(mockRequestor)
        .withUserLocale(Locale.getDefault().toString()).build();

    DbxClientV2 client = new DbxClientV2(config, "fakeAccessToken");

    HttpRequestor.Uploader mockUploader = mockUploader();
    when(mockUploader.finish()).thenReturn(createSuccessResponse(expected.toString()));
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
