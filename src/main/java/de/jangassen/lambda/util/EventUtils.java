package de.jangassen.lambda.util;

import de.jangassen.lambda.parser.yaml.SamTemplate;
import org.apache.commons.lang3.StringUtils;

public final class EventUtils {
    public static final String API = "API";

    private EventUtils() {
    }

    public static boolean isAPIEvent(SamTemplate.Event event) {
        return StringUtils.equalsIgnoreCase(API, event.Type);
    }

}
