package de.jangassen.lambda.loader;

import java.lang.reflect.Method;

public class MethodInvocationContext {
    private final Method method;
    private final Object instance;
    private final ClassLoader classLoader;
    private final Class<?> parameterClass;

    MethodInvocationContext(Method method, Object instance, ClassLoader classLoader, Class<?> parameterClass) {
        this.method = method;
        this.instance = instance;
        this.classLoader = classLoader;
        this.parameterClass = parameterClass;
    }

    public Method getMethod() {
        return method;
    }

    public Object getInstance() {
        return instance;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public Class<?> getParameterClass() {
        return parameterClass;
    }
}
