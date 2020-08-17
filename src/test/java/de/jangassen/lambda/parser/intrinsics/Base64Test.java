package de.jangassen.lambda.parser.intrinsics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base64Test {

    @Test
    public void testBase64() {
        String result = new Base64().apply("test");

        assertEquals("dGVzdA==", result);
    }
}