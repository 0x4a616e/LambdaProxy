package de.jangassen.lambda.parser;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public class SamPropertyUtils extends PropertyUtils {
    public SamPropertyUtils() {
        setSkipMissingProperties(true);
    }

    @Override
    public Property getProperty(Class<?> type, String name) {
        return super.getProperty(type, getPropertyName(name));
    }

    @Override
    public Property getProperty(Class<?> type, String name, BeanAccess bAccess) {
        return super.getProperty(type, getPropertyName(name), bAccess);
    }

    private String getPropertyName(String name) {
        return StringUtils.uncapitalize(name);
    }
}
