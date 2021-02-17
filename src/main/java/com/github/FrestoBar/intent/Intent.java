package com.github.FrestoBar.intent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a class represents a RA Robot Utilities command.
 * For example, {@link ZipCommand} represents '-zip' command.
 * <strong>Need to use before constructor.</strong><br>
 * For example
 * <pre>
 *     -help,
 *     -zip,
 *     -unzip,
 *     -convert,
 *     etc.
 * <pre/>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface Intent {
    /**
     * Expected command value which this class will catch.
     * @return command value.
     */
    String value();
}
