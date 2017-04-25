/**
 * 
 */
package com.dropboxclient.commands;

import static com.dropboxclient.util.Utils.indent;
import static com.dropboxclient.util.Utils.repeat;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropboxclient.commands.annotations.Parameter;
import com.dropboxclient.commands.annotations.Parameters;
import com.dropboxclient.shell.Console;
import com.dropboxclient.util.TabularFormater;
import com.dropboxclient.util.Utils;

/**
 * @author Peter Jerold Leslie
 *
 */
@Parameters(commandName = Commands.LIST, commandDescription = "This command list files and folders within the specified directory and some metadata about each entry")
public class ListCommand extends AbtractCommandImpl {
  /**
   * log
   */
  private static final Logger LOG = Logger.getLogger(ListCommand.class);
  
  /**
   * Dropbox app access token
   */
  @Parameter(description = "Authorization code, which could be generated using auth command", mandatory = true, isToken = true)
  public String accessToken;
  /**
   * Dropbox path
   */
  @Parameter(description = "Path to a file or folder to list details about. Path should be in double quotes", mandatory = true)
  public String path = "";

  /**
   * @param console
   */
  public ListCommand(Console console) {
    super(ListCommand.class);
    super.console = console;
    LOG.info("InfoCommand initiated using Console");
  }

  /**
   * @param client
   * @param out
   */
  public ListCommand(DbxClientV2 client, PrintStream out) {
    super(ListCommand.class);
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
  public  List<Result> execute() throws Exception {
    LOG.info("start executing list command");
    path = path.trim();
    if (path.equals("/")) {
      path = "";
    }
    List<Result> resultEntries = new ArrayList<>();
    
    try {
      ListFolderResult result = client.files().listFolder(path);
      FolderMetadata pfolderMetadata = null;
      if (!path.equals("")) {
        pfolderMetadata = (FolderMetadata) client.files().getMetadata(path);
      }
      resultEntries.add(new Result(pfolderMetadata,false));

      while (true) {
        for (Metadata metadata : result.getEntries()) {
          if (metadata instanceof FileMetadata) {
            FileMetadata fileMetadata = (FileMetadata) metadata;
            resultEntries.add(new Result(fileMetadata,true));
          }

          if (metadata instanceof FolderMetadata) {
            FolderMetadata folderMetadata = (FolderMetadata) metadata;
            resultEntries.add(new Result(folderMetadata,true));
          }
        }

        if (!result.getHasMore()) {
          break;
        }
        
        result = client.files().listFolderContinue(result.getCursor());
      }
    } catch (Exception e) {
      if (e instanceof ListFolderErrorException) {
        FileMetadata metadata = (FileMetadata) client.files().getMetadata(path);
        resultEntries.add(new Result(metadata,false));
      } else {
        console.print(e.getMessage());
        throw e;
      }

    }
    LOG.info("executing list command completed");
    return resultEntries;

  }

  /**
   * @param formatter
   * @param metadata
   * @param relative
   */
  void addFolderRow(TabularFormater formatter, FolderMetadata metadata, boolean relative) {
    LOG.info("Adding tabular row for folder " + metadata.getPathDisplay());
    String path = "/";
    if (metadata != null)
      path = relative ? repeat(" ", indent) + "- " + metadata.getPathDisplay() : metadata.getPathDisplay();
    formatter.addRow(path, "dir", "-", "-");
  }

  /**
   * @param formatter
   * @param metadata
   * @param relative
   */
  void addFileRow(TabularFormater formatter, FileMetadata metadata, boolean relative) {
    LOG.info("Adding tabular row for file" + metadata.getPathDisplay());
    String path = relative ? repeat(" ", indent) + "- " + metadata.getPathDisplay() : metadata.getPathDisplay();
    formatter.addRow(path, "file", Utils.readableFileSize(metadata.getSize()), metadata.getServerModified());
  }
 
  /* (non-Javadoc)
   * @see com.dropboxclient.commands.AbstractCommand#print(java.lang.Object)
   */
  @Override
  public void print(Object object) throws Exception {
    LOG.info("printing list command result on console");
    TabularFormater formatter = new TabularFormater("Path", "Type", "Size", "Modified at");
    List<Result> results = (List<Result>) object;
    for (Result result : results) {
      Metadata metadata = result.getMetadata();
      if (metadata instanceof FileMetadata) {
        FileMetadata fileMetadata = (FileMetadata) metadata;
        addFileRow(formatter, fileMetadata, result.isRelative());
      }

      if (metadata instanceof FolderMetadata) {
        FolderMetadata folderMetadata = (FolderMetadata) metadata;
        addFolderRow(formatter, folderMetadata, result.isRelative());
      }
    }
    console.h1("List of file or folder for the given path in your dropbox");
    console.println("");
    console.println(formatter.getFormatText());
    LOG.info("printing list command result on console completed");
  }
  //Supporting class to save dropbox path list results
  class Result{
    
    /**
     * @param metadata
     * @param relative
     */
    public Result(Metadata metadata, boolean relative) {
      super();
      this.metadata = metadata;
      this.relative = relative;
    }
    
    /**
     * Dropbox file or folder Metadata
     */
    private Metadata metadata;
    /**
     * 
     */
    private boolean relative;
    /**
     * @return
     */
    public Metadata getMetadata() {
      return metadata;
    }
    /**
     * @param metadata
     */
    public void setMetadata(Metadata metadata) {
      this.metadata = metadata;
    }
    /**
     * @return
     */
    public boolean isRelative() {
      return relative;
    }
    /**
     * @param relative
     */
    public void setRelative(boolean relative) {
      this.relative = relative;
    }
  }
  
}

