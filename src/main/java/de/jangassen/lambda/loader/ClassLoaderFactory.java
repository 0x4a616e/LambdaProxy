package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.ApiResource;

@FunctionalInterface
public interface ClassLoaderFactory {
    ClassLoader create(ApiResource handler);
}
