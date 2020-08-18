package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.ApiResource;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SamArtifactResolverTest {
    @Test
    void testArtifactResolver() {
        SamArtifactResolver samArtifactResolver = new SamArtifactResolver(
                new File("dir").toPath(), Stream::of);

        ApiResource apiResource = new ApiResource("resource", "codeUri", "handler");
        List<URL> resolvedResources = samArtifactResolver.resolve(apiResource);

        List<URL> expectedResources = Stream.of(new File("dir/resource/lib"), new File("dir/resource"))
                .map(File::toPath)
                .map(SamArtifactResolver::toURL)
                .collect(Collectors.toList());

        assertEquals(expectedResources, resolvedResources);
    }
}