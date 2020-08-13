package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.ApiInvocation;

import java.net.URL;
import java.util.List;

@FunctionalInterface
public interface ArtifactResolver {
    List<URL> resolve(ApiInvocation handler);
}
