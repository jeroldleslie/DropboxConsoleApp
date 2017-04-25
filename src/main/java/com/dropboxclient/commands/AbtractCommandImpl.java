/**
 * 
 */
package com.dropboxclient.commands;

import static com.dropboxclient.util.Utils.indent;
import static com.dropboxclient.util.Utils.repeat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropboxclient.commands.annotations.Parameter;
import com.dropboxclient.commands.annotations.Parameters;
import com.dropboxclient.util.TabularFormater;

/**
 * @author Peter Jerold Leslie
 *
 */
abstract public class AbtractCommandImpl extends AbstractCommand {
  /**
   * log
   */
  private static final Logger LOG = Logger.getLogger(AbtractCommandImpl.class);
  /**
   * class type to identify annotated fields to parse and validate
   */
  final Class<?>              type;
  /**
   * 
   */
  @Parameter(description = "locale", mandatory = false)
  public String               locale;
  protected DbxRequestConfig  config;
  /**
   * 
   */
  protected DbxClientV2       client;
  /**
   * 
   */
  private String              accessToken;

  /**
   * @param type
   *          class type of command class
   */
  public AbtractCommandImpl(Class<?> type) {
    LOG.info("AbtractCommandImpl initiated");
    this.type = type;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.dropboxclient.commands.AbstractCommand#parseAndBuild(java.lang.Object,
   * java.lang.String[])
   */
  @Override
  public void parseAndBuild(Object object, String[] args) throws Exception {
    LOG.info("start parsing and build dropbox client for command");
    Field fs[] = type.getFields();
    int mandatoryFieldLength = 0;
    int fieldIndex = 0;
    for (Field f : fs) {
      Annotation a = f.getAnnotation(Parameter.class);
      if (a != null) {
        LOG.info("validating and assigning values for " + f.getName());
        Parameter p = (Parameter) a;
        if (p.mandatory()) {
          mandatoryFieldLength++;
        }

        Object value;
        try {
          if (args != null) {
            value = args[fieldIndex];
          } else {
            value = null;
          }
        } catch (Exception e) {
          value = null;
        }

        f.set(object, value);

        if (p.isToken()) {
          if (value != null)
            this.accessToken = value.toString();
        }
        fieldIndex++;
      }
    }

    if ((mandatoryFieldLength > 0 && args == null) || (args != null && args.length < mandatoryFieldLength)) {
      LOG.info("throws exception when mandatory args missing");
      throw new Exception("Required parameters missing");
    }

    createDroboxClient();
    LOG.info("parsing and build dropbox client for command completed successfully");
  }

  /**
   * creates v2 dropbox client (DbxClientV2) if null
   */
  private void createDroboxClient() {
    LOG.info("creating DbxClientV2");
    if (client == null) {
      if (locale == null) {
        locale = Locale.getDefault().toString();
      }
      LOG.info("creating DbxRequestConfig");
      config = new DbxRequestConfig("DropboxClient/console", locale);
      LOG.info("DbxRequestConfig created");
      if (this.accessToken != null) {
        client = new DbxClientV2(config, accessToken);
      }
      LOG.info("DbxClientV2 created");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.dropboxclient.commands.Command#printUsage()
   */
  @Override
  public String usage(int startIndent) {
    LOG.info("building command usage");
    TabularFormater formatter = new TabularFormater("Parameter", "Description", "Required");
    StringBuilder builder = new StringBuilder();
    StringBuilder usage = new StringBuilder();
    StringBuilder params = new StringBuilder();
    builder.append("\n");
    builder.append(repeat(" ", startIndent));
    Annotation[] annotations = this.type.getAnnotations();
    for (Annotation annotation : annotations) {
      if (annotation instanceof Parameters) {
        Parameters a = (Parameters) annotation;
        builder.append(a.commandName()).append(repeat(" ", indent)).append(a.commandDescription()).append("\n");
        usage.append(repeat(" ", startIndent + indent)).append("Usage: <main class> ").append(a.commandName());
        if (!a.commandName().equals(Commands.HELP)) {
          Field fs[] = type.getFields();
          for (Field f : fs) {
            Annotation fa = f.getAnnotation(Parameter.class);
            if (fa != null) {
              Parameter fp = (Parameter) fa;
              usage.append(repeat(" ", indent)).append("{").append(f.getName()).append("}");
              formatter.addRow(f.getName(), fp.description(), fp.mandatory());
            }
          }
          if (formatter != null) {
            params.append(indent(formatter.getFormatText(), startIndent + indent * 2));
          }
        } else {

        }
      }
    }

    builder.append(usage.toString()).append("\n");
    builder.append(params.toString());
    builder.append("\n");
    LOG.info("command usage builded successfully");
    return builder.toString();
  }

  /**
   * @param toIndent
   * @param indents
   * @return
   */
  private String indent(String toIndent, int indents) {
    StringBuilder sb = new StringBuilder();
    for (String x : toIndent.split("\n")) {
      sb.append(repeat(" ", indents) + x + "\n");
    }
    return sb.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.dropboxclient.commands.AbstractCommand#usage(java.util.Map)
   */
  @Override
  public void usage(Map<String, Object> commands) throws IOException {

  }

}
