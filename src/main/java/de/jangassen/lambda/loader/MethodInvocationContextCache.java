package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.ApiResource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MethodInvocationContextCache implements MethodInvocationContextProvider {
    private final ClassLoaderFactory classLoaderFactory;

    private final Map<ApiResource, MethodInvocationContext> cache = new ConcurrentHashMap<>();

    public MethodInvocationContextCache(ClassLoaderFactory classLoaderFactory) {
        this.classLoaderFactory = classLoaderFactory;
    }

    @Override
    public MethodInvocationContext getMethodInvocationContext(ApiResource apiResource) {
        return cache.computeIfAbsent(apiResource, key -> MethodInvocationContextBuilder.start(classLoaderFactory).withApiResource(key).build());
    }
}
