//CHECKSTYLE:OFF
package retrofit2;
//CHECKSTYLE:ON

import java.lang.reflect.Type;

/**
 * @hide
 */
public class JsonApiUtils {
    public static Class<?> getRawType(final Type type) {
        return Utils.getRawType(type);
    }
}
