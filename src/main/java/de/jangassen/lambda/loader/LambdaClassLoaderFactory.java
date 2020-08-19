package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.ApiResource;
import de.jangassen.lambda.exception.LambdaInvocationException;
import de.jangassen.lambda.lambda.LambdaProxyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LambdaClassLoaderFactory implements ClassLoaderFactory, AutoCloseable {
    private final Logger logger = LoggerFactory.getLogger(LambdaClassLoaderFactory.class);
    private final Set<ClassLoader> classLoaders = new HashSet<>();
    private final ArtifactResolver artifactResolver;

    public LambdaClassLoaderFactory(ArtifactResolver artifactResolver) {
        this.artifactResolver = artifactResolver;
    }

    @Override
    public ClassLoader create(ApiResource handler) {
        try {
            LambdaClassLoader lambdaClassLoader = new LambdaClassLoader(artifactResolver.resolve(handler));
            classLoaders.add(lambdaClassLoader);
            lambdaClassLoader.addClass(LambdaProxyContext.class);
            return lambdaClassLoader;
        } catch (Exception e) {
            throw new LambdaInvocationException(e);
        }
    }

    @Override
    public void close(ClassLoader classLoader) {
        if (classLoader instanceof LambdaClassLoader) {
            try {
                ((LambdaClassLoader) classLoader).close();
            } catch (IOException e) {
                logger.error("Unable to close ClassLoader", e);
            } finally {
                classLoaders.remove(classLoader);
            }
        }
    }

    @Override
    public void close() {
        new HashSet<>(classLoaders).forEach(this::close);
        classLoaders.clear();
    }
}
