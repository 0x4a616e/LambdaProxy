package de.jangassen.lambda.util;

import de.jangassen.lambda.parser.yaml.SamTemplate;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParameterUtilsTest {

    @Test
    void testResolve() {
        SamTemplate samTemplate = new SamTemplate();
        samTemplate.setParameters(Collections.singletonMap("test", Collections.singletonMap("Default", "123")));

        String resolvedString = ParameterUtils.resolve("Test ${test}", samTemplate);

        assertEquals("Test 123", resolvedString);
    }
}