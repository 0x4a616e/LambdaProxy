package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.RequestEvent;

@FunctionalInterface
public interface ClassLoaderFactory {
    ClassLoader create(RequestEvent handler);
}
