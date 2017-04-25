/**
 * 
 */
package com.dropboxclient.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Peter Jerold Leslie
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameters {
  /**
   * @return
   */
  String commandName() default "";
  /**
   * @return
   */
  String commandDescription() default "";
}
