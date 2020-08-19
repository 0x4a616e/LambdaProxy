package de.jangassen.lambda.loader;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import de.jangassen.lambda.api.ApiResource;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.time.Duration;

public class MethodInvocationContextCache implements MethodInvocationContextProvider {
    private final LoadingCache<ApiResource, MethodInvocationContext> cache;
    private final ClassLoaderFactory classLoaderFactory;

    public MethodInvocationContextCache(ClassLoaderFactory classLoaderFactory, Duration timeout) {
        this.classLoaderFactory = classLoaderFactory;
        cache = CacheBuilder.newBuilder()
                .expireAfterAccess(timeout)
                .removalListener(this::onRemove)
                .build(new CacheLoader<ApiResource, MethodInvocationContext>() {
                    @Override
                    public MethodInvocationContext load(@Nonnull ApiResource apiResource) {
                        return MethodInvocationContextBuilder
                                .start(classLoaderFactory)
                                .withApiResource(apiResource)
                                .build();
                    }
                });
    }

    private void onRemove(RemovalNotification<ApiResource, MethodInvocationContext> removedEntry) {
        ClassLoader classLoader = removedEntry.getValue().getClassLoader();
        if (classLoader instanceof Closeable) {
            classLoaderFactory.close(classLoader);
        }
    }

    @Override
    public MethodInvocationContext getMethodInvocationContext(ApiResource apiResource) {
        return cache.getUnchecked(apiResource);
    }
}
