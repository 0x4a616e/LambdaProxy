package de.jangassen.lambda.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class AccessLogger {
    private static final Logger logger = LoggerFactory.getLogger(AccessLogger.class);

    public static void logAccess(HttpServletRequest req, Integer statusCode, int length) {
        logger.info("{} {} {} {} {}", req.getMethod().toUpperCase(), req.getPathInfo(), req.getProtocol(), statusCode, length);
    }
}
