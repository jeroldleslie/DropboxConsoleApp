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
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {
  /**
   * @return
   */
  String description() default "";
  /**
   * @return
   */
  boolean mandatory() default false;
  /**
   * @return
   */
  boolean isToken() default false;
}
