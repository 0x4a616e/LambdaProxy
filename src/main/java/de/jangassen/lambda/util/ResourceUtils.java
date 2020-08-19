package de.jangassen.lambda.util;

import de.jangassen.lambda.parser.yaml.SamTemplate;
import org.apache.commons.lang3.StringUtils;

public final class ResourceUtils {
    private static final String JAVA_8 = "java8";

    private ResourceUtils() {
    }

    public static boolean isJava8Runtime(SamTemplate.Resource resource) {
        return StringUtils.equalsIgnoreCase(JAVA_8, resource.Properties.Runtime);
    }
}
