package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.ApiInvocation;

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

    public SamArtifactResolver(Path rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public List<URL> resolve(ApiInvocation handler) {
        Path samBuildPath = rootPath.resolve(handler.getResourceName());

        List<URL> paths = getLibs(samBuildPath.resolve(LIB));
        paths.add(toURL(samBuildPath));
        return paths;
    }

    private List<URL> getLibs(Path libPath) {
        try (Stream<Path> files = Files.list(libPath)) {
            return files.map(this::toURL).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URL toURL(Path path) {
        try {
            return path.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
