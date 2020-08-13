package de.jangassen.lambda.util;

import de.jangassen.lambda.yaml.SamTemplate;
import org.apache.commons.lang3.StringUtils;

public class ResourceUtils {
    private static final String JAVA_8 = "java8";
    
    public static boolean isJava8Runtime(SamTemplate.Resource resource) {
        return StringUtils.equalsIgnoreCase(JAVA_8, resource.Properties.Runtime);
    }
}
