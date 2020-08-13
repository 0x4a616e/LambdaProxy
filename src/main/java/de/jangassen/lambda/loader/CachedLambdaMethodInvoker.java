package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.RequestEvent;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedLambdaMethodInvoker extends DefaultLambdaMethodInvoker {
    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Map<List<Class<?>>, Method> handlerMethods = new HashMap<>();
    private final Map<String, ClassLoader> classLoaders = new HashMap<>();

    public CachedLambdaMethodInvoker(ClassLoaderFactory classLoaderFactory) {
        super(classLoaderFactory);
    }

    @Override
    protected Object getHandlerInstance(Class<?> handlerClass) {
        return instances.computeIfAbsent(handlerClass, super::getHandlerInstance);
    }

    @Override
    protected Method getHandlerMethod(RequestEvent requestEvent, Class<?> handlerClass, Class<?> parameterClass) {
        return handlerMethods.computeIfAbsent(Arrays.asList(handlerClass, parameterClass),
                key -> super.getHandlerMethod(requestEvent, handlerClass, parameterClass));
    }

    @Override
    protected ClassLoader getClassLoader(RequestEvent requestEvent) {
        return classLoaders.computeIfAbsent(requestEvent.getCodeUri(), codeUri -> super.getClassLoader(requestEvent));
    }
}
