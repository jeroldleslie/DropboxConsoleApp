/**
 * 
 */
package mocks;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.HttpRequestor;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.users.FullAccount;

/**
 * @author Peter Jerold Leslie
 *
 */
public class DropboxMock {

  public static DbxClientV2 mockDbxClientV2ForInfo(FullAccount expectedFullAccount) throws IOException {
    HttpRequestor mockRequestor = mock(HttpRequestor.class);
    DbxRequestConfig config = createRequestConfig().withAutoRetryEnabled(3).withHttpRequestor(mockRequestor)
        .withUserLocale(Locale.getDefault().toString()).build();

    DbxClientV2 client = new DbxClientV2(config, "fakeAccessToken");

    HttpRequestor.Uploader mockUploader = mockUploader();
    when(mockUploader.finish()).thenReturn(createSuccessResponse(expectedFullAccount.toString()));
    when(mockRequestor.startPost(anyString(), anyHeaders())).thenReturn(mockUploader);
    return client;
  }

  public static DbxClientV2 mockDbxClientV2ForList(ListFolderResult expectedFolderResult,
      FolderMetadata expectedFolderMetadata) throws IOException {
    HttpRequestor mockRequestor = mock(HttpRequestor.class);
    DbxRequestConfig config = createRequestConfig().withAutoRetryEnabled(3).withHttpRequestor(mockRequestor)
        .withUserLocale(Locale.getDefault().toString()).build();

    DbxClientV2 client = new DbxClientV2(config, "fakeAccessToken");

    HttpRequestor.Uploader mockUploader = mockUploader();
    when(mockUploader.finish()).thenReturn(createSuccessResponse(expectedFolderResult.toString()))
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
