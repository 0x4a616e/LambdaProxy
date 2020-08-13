package de.jangassen.lambda.loader;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.jangassen.lambda.api.ApiResource;

import javax.annotation.Nonnull;
import java.time.Duration;

public class MethodInvocationContextCache implements MethodInvocationContextProvider {
    private final LoadingCache<ApiResource, MethodInvocationContext> cache;

    public MethodInvocationContextCache(ClassLoaderFactory classLoaderFactory, Duration timeout) {
        cache = CacheBuilder.newBuilder()
                .expireAfterAccess(timeout)
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

    @Override
    public MethodInvocationContext getMethodInvocationContext(ApiResource apiResource) {
        return cache.getUnchecked(apiResource);
    }
}
