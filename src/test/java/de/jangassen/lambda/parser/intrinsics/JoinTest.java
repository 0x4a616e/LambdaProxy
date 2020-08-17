package de.jangassen.lambda.parser.intrinsics;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JoinTest {
    @Test
    public void testJoin() {
        Object result = new Join().apply(Arrays.asList(" ", Arrays.asList("just", "a", "test")));

        assertEquals("just a test", result);
    }
}