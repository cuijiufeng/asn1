package com.inferiority.asn1.analysis.util;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.util.Objects;

/**
 * @author cuijiufeng
 * @date 2023/3/19 11:01
 */
public class AopUtil {

    @SuppressWarnings("unchecked")
    public static <T> T proxyObject(Class<T> clazz, String methodName, MethodInterceptor interceptor) {
        Objects.requireNonNull(clazz, "clazz can't be null");
        Objects.requireNonNull(methodName, "method name can't be null");
        // 创建Enhancer代理,来代理Student类
        return (T) Enhancer.create(clazz, (MethodInterceptor) (obj, method, args, proxy) -> {
            if (methodName.equals(method.getName())) {
                return interceptor.intercept(obj, method, args, proxy);
            }
            return proxy.invokeSuper(obj, args);
        });
    }
}
