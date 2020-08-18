package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.ApiResource;
import de.jangassen.lambda.exception.LambdaInvocationException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SamArtifactResolver implements ArtifactResolver {
    public static final String LIB = "lib";

    private final Path rootPath;
    private final ThrowingFunction<Path, Stream<Path>> listFiles;

    public SamArtifactResolver(Path rootPath) {
        this(rootPath, Files::list);
    }

    SamArtifactResolver(Path rootPath, ThrowingFunction<Path, Stream<Path>> listFiles) {
        this.rootPath = rootPath;
        this.listFiles = listFiles;
    }

    @Override
    public List<URL> resolve(ApiResource handler) {
        Path samBuildPath = rootPath.resolve(handler.getResourceName());

        List<URL> paths = getLibs(samBuildPath.resolve(LIB));
        paths.add(toURL(samBuildPath));
        return paths;
    }

    private List<URL> getLibs(Path libPath) {
        try (Stream<Path> files = listFiles.invoke(libPath)) {
            return files.map(SamArtifactResolver::toURL).collect(Collectors.toList());
        } catch (IOException e) {
            throw new LambdaInvocationException(e);
        }
    }

    static URL toURL(Path path) {
        try {
            return path.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new LambdaInvocationException(e);
        }
    }

    @FunctionalInterface
    interface ThrowingFunction<P, R> {
        R invoke(P p) throws IOException;
    }
}
