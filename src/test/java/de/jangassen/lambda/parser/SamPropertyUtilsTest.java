package de.jangassen.lambda.parser;

import de.jangassen.lambda.parser.yaml.SamTemplate;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SamPropertyUtilsTest {
    @Test
    void testGetProperty() {
        Property property = new SamPropertyUtils().getProperty(SamTemplate.class, "Transform");

        assertEquals("transform", property.getName());
    }

    @Test
    void testGetPropertyWithDefaultAccess() {
        Property property = new SamPropertyUtils().getProperty(SamTemplate.class, "Transform", BeanAccess.DEFAULT);

        assertEquals("transform", property.getName());
    }
}