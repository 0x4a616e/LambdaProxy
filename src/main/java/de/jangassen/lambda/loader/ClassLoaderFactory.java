package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.ApiInvocation;

@FunctionalInterface
public interface ClassLoaderFactory {
    ClassLoader create(ApiInvocation handler);
}
