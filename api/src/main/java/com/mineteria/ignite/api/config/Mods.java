package com.mineteria.ignite.api.config;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify the mod target paths.
 *
 * <p>By default it is `./mods` at the root directory. However this
 * can be modified with startup arguments.</p>
 */
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Mods {
}
