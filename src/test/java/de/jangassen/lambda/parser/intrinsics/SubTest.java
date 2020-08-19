package de.jangassen.lambda.parser.intrinsics;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubTest {
    @Test
    public void testSub() {
        String result = new Sub(Collections.singletonMap("param", "test")).apply("just a ${param}");

        assertEquals("just a test", result);
    }
}