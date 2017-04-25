/**
 * 
 */
package com.dropboxclient.shell;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.Logger;
/**
 * @author Peter Jerold Leslie
 *
 */
public class Console {
  /**
   * log
   */
  private static final Logger LOG = Logger.getLogger(Console.class);
  
  /**
   * console output using PrintStream
   */
  protected PrintStream out;

  /**
   * 
   */
  public Console() {
    this.out = new PrintStream(System.out);
    LOG.info("Console initiated");
  }
  
  /**
   * @param out
   */
  public Console(PrintStream out) {
    this.out = out;
    LOG.info("Console initiated using PrintStream");
  }

  /**
   * @param line
   * @throws IOException
   */
  public void print(String line) throws IOException {
    out.append(line);
  }

  /**
   * @param line
   * @throws IOException
   */
  public void println(String line) throws IOException {
    out.append(line).append('\n');
  }

  /**
   * @param title
   * @throws IOException
   */
  public void h1(String title) throws IOException {
    out.append(title).append('\n');
    String underline = title.replaceAll(".", "*");
    out.append(underline).append("\n");
  }
  
  /**
   * @param title
   * @throws IOException
   */
  public void h2(String title) throws IOException {
    out.append(title).append('\n');
    String underline = title.replaceAll(".", "-");
    out.append(underline).append("\n");
  }
}
