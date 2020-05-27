package de.jangassen.lambda.util;

import org.apache.commons.io.IOUtils;
import org.springframework.core.ConfigurableObjectInputStream;

import java.io.*;

public final class ClassLoaderUtils {
    public static Object moveToClassLoader(ClassLoader classLoader, Object object) throws IOException, ClassNotFoundException {
        return getObjectInClassLoader(classLoader, getBytes(object));
    }

    public static Object getObjectInClassLoader(ClassLoader classLoader, byte[] data) throws IOException, ClassNotFoundException {
        return new ConfigurableObjectInputStream(new ByteArrayInputStream(data), classLoader).readObject();
    }

    public static byte[] getBytes(Object object) throws IOException {
        ByteArrayOutputStream file = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(file);
        out.writeObject(object);
        return file.toByteArray();
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
