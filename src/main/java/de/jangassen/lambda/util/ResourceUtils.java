package de.jangassen.lambda.util;

import de.jangassen.lambda.parser.yaml.SamTemplate;
import org.apache.commons.lang3.StringUtils;

public final class ResourceUtils {
    private static final String JAVA_8 = "java8";
    private static final String JAVA_11 = "java11";

    private ResourceUtils() {
    }

    public static boolean isSupportedRuntime(SamTemplate.Resource resource) {
        if (StringUtils.equalsIgnoreCase(JAVA_8, resource.getProperties().getRuntime())) {
            return true;
        }

        String majorVersionString = System.getProperty("java.version").split("\\.")[0];
        int version = Integer.parseInt(majorVersionString);
        if (version >= 11) {
            return StringUtils.equalsIgnoreCase(JAVA_11, resource.getProperties().getRuntime());
        }

        return false;
    }
}
