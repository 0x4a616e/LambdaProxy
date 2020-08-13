package de.jangassen.lambda.util;

import org.apache.commons.io.IOUtils;
import org.powermock.classloading.DeepCloner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public final class ClassLoaderUtils {
    public static Object moveToClassLoader(ClassLoader classLoader, Object object) {
        DeepCloner deepCloner = new DeepCloner(classLoader);
        return deepCloner.clone(object);
    }

    public static byte[] getClassBytes(Class<?> clazz) throws IOException {
        String resourceName = getResourceName(clazz);
        InputStream resourceAsStream = ClassLoaderUtils.class.getClassLoader().getResourceAsStream(resourceName);
        if (resourceAsStream == null) {
            throw new FileNotFoundException(resourceName);
        }
        return IOUtils.toByteArray(resourceAsStream);
    }

    private static String getResourceName(Class<?> clazz) {
        return clazz.getName().replace('.', '/') + ".class";
    }
}
