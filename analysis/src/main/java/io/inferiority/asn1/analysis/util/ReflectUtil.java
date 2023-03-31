package io.inferiority.asn1.analysis.util;

import net.sf.cglib.beans.BeanCopier;

import java.lang.reflect.Method;

/**
 * @author cuijiufeng
 * @date 2023/3/19 10:46
 */
public class ReflectUtil {

    public static <T> T cast(Class<T> clazz, Object obj) {
        return clazz.cast(obj);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Method method, Object obj, Object ... args) throws ReflectiveOperationException {
        return (T) method.invoke(obj, args);
    }

    public static void deepCopyBean(Class<?> clazz, Object src, Object tar) {
        BeanCopier.create(clazz, clazz, false)
                .copy(src, tar, null);
    }
}
