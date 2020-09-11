package de.jangassen.lambda.logger;

import de.jangassen.lambda.api.ApiMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ApiLogger {
    private static final Logger logger = LoggerFactory.getLogger(ApiLogger.class);

    public static void logApiMethods(List<ApiMethod> apiMethods) {
        apiMethods.forEach(e -> logger.info("* {}", e));
    }
}
