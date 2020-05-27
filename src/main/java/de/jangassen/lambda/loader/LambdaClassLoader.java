package de.jangassen.lambda.loader;

import de.jangassen.lambda.util.ClassLoaderUtils;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class LambdaClassLoader extends URLClassLoader {
    public LambdaClassLoader(List<URL> urls) {
        super(urls.toArray(new URL[0]), ClassLoader.getSystemClassLoader().getParent());
    }

    public void addClass(Class<?> clazz) throws IOException {
        byte[] bytes = ClassLoaderUtils.getClassBytes(clazz);
        defineClass(clazz.getName(), bytes, 0, bytes.length);
    }
}
