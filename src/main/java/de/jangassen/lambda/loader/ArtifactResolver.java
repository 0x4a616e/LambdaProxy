package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.RequestEvent;

import java.net.URL;
import java.util.List;

@FunctionalInterface
public interface ArtifactResolver {
    List<URL> resolve(RequestEvent handler);
}
