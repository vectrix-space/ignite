package com.mineteria.ignite.api.config.path;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify the mod configuration paths.
 *
 * <p>By default it is `./configs` at the root directory. However this
 * can be modified with startup arguments.</p>
 */
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ConfigsPath {
}
