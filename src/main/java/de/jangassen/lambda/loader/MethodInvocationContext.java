package de.jangassen.lambda.loader;

import java.lang.reflect.Method;

public class MethodInvocationContext {
    private final Method method;
    private final Object instance;
    private final ClassLoader classLoader;
    private final Class<?> parameterClass;
    private final InvocationStrategy invocationStrategy;

    MethodInvocationContext(Method method, Object instance, ClassLoader classLoader, Class<?> parameterClass, InvocationStrategy invocationStrategy) {
        this.method = method;
        this.instance = instance;
        this.classLoader = classLoader;
        this.parameterClass = parameterClass;
        this.invocationStrategy = invocationStrategy;
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

    public InvocationStrategy getInvocationStrategy() {
        return invocationStrategy;
    }
}
