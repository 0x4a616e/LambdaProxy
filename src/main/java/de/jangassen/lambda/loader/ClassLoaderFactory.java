package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.ApiResource;

public interface ClassLoaderFactory {
    ClassLoader create(ApiResource handler);

    void close(ClassLoader classLoader);
}
