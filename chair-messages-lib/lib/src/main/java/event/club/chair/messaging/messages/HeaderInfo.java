package event.club.chair.messaging.messages;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HeaderInfo {

    // String aliases
    // String version

    /**
     * Defines alternate keys that can be used to determine the class to parse into a Message. This is useful when a
     * Message's name changes or the package shifts. Note that keys must be unique within a classpath.
     *
     * In general, the first alias in the array will be the one used when broadcast, any additional will still be
     * recognized for backwards-compatibility. For example a value of ["foo", "bar", "baz"] will cause messages to be
     * emitted with a key of 'foo', but if a message is received with the key of 'bar' or 'baz' it will be recognized.
     *
     * By default, the simple class name of the Message will be used for identification.
     *
     * @return an array of Strings defining alternate names.
     */
    String[] aliases() default "";

    /**
     * The version of this Message. Downstream handlers can make use if this flag to signal how a Message should be
     * interpreted.
     *
     * @return
     */
    String version() default "1";
}
