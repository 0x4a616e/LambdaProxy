package de.jangassen.lambda.util;

import de.jangassen.lambda.exception.LambdaInvocationException;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

public final class PathUtils {
    private PathUtils() {
    }

    public static URL toURL(Path path) {
        try {
            return path.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new LambdaInvocationException(e);
        }
    }
}
