package com.mineteria.ignite.api.event;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify an event listener, with the specified
 * {@link PostPriority} priority.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
  /**
   * Returns the listener specified priority.
   *
   * @return The listener priority
   */
  @NonNull PostPriority priority() default PostPriority.NORMAL;
}
