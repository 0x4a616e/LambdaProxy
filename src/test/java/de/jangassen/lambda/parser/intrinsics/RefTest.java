package de.jangassen.lambda.parser.intrinsics;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RefTest {

    @Test
    public void testRef() {
        String result = new Ref(Collections.singletonMap("key", "value")).apply("key");

        assertEquals("value", result);
    }
}