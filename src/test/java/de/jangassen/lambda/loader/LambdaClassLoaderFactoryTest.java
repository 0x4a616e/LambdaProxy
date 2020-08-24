package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.ApiResource;
import de.jangassen.lambda.util.PathUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LambdaClassLoaderFactoryTest {

    @Test
    void testClassLoaderFactory() throws ClassNotFoundException {
        LambdaClassLoaderFactory classLoaderFactory = new LambdaClassLoaderFactory(api -> getClassPathUrls());

        ApiResource apiResource = new ApiResource("name", "codeUri", "handler");
        ClassLoader classLoader1 = classLoaderFactory.create(apiResource);
        classLoader1.loadClass(LambdaClassLoaderFactory.class.getName());

        ClassLoader classLoader2 = classLoaderFactory.create(apiResource);
        classLoaderFactory.close();

        assertThrows(ClassNotFoundException.class,
                () -> classLoader2.loadClass(LambdaClassLoaderFactory.class.getName()));
    }

    private List<URL> getClassPathUrls() {
        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(File.pathSeparator);
        return Arrays.stream(classpathEntries)
                .map(File::new)
                .map(File::toPath)
                .map(PathUtils::toURL)
                .collect(Collectors.toList());
    }
}