package de.jangassen.lambda.util;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import de.jangassen.lambda.loader.LambdaClassLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ClassLoaderUtilsTest {

    @Test
    public void testGetClassBytes() throws IOException {
        byte[] classBytes = ClassLoaderUtils.getClassBytes(Object.class);

        byte[] expectedMagic = new byte[]{(byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe};
        byte[] actualMagic = Arrays.copyOfRange(classBytes, 0, 4);

        assertArrayEquals(expectedMagic, actualMagic);
    }

    @Test
    public void testMoveToClassLoader() throws IOException, ClassNotFoundException {
        LambdaClassLoader urlClassLoader = new LambdaClassLoader(Collections.emptyList());
        urlClassLoader.addClass(TestClass.class);
        urlClassLoader.addClass(APIGatewayProxyRequestEvent.class);
        urlClassLoader.addClass(Context.class);

        TestClass testClass = new TestClass();
        Object testObject = ClassLoaderUtils.moveToClassLoader(urlClassLoader, testClass);

        assertEquals(urlClassLoader, testObject.getClass().getClassLoader());
        assertNotEquals(testClass.getClass(), testObject.getClass());
    }
}