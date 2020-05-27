package de.jangassen.lambda;

import de.jangassen.lambda.api.ApiDescription;
import de.jangassen.lambda.api.RequestEvent;
import de.jangassen.lambda.loader.LambdaMethodInvoker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

class LambdaProxyServlet extends HttpServlet {

    public static final String GET_BODY = "getBody";
    public static final String GET_STATUS_CODE = "getStatusCode";

    private final Logger logger = LoggerFactory.getLogger(LambdaProxyServlet.class);

    private final LambdaMethodInvoker lambdaMethodInvoker;
    private final ApiDescription apiDescription;

    public LambdaProxyServlet(LambdaMethodInvoker lambdaMethodInvoker, ApiDescription apiDescription) {
        this.apiDescription = apiDescription;
        this.lambdaMethodInvoker = lambdaMethodInvoker;

        apiDescription.listAPIs().entrySet().stream().forEach(e -> {
            logger.info("* {} [{}]", e.getKey(), e.getValue().stream()
                    .map(StringUtils::upperCase).collect(Collectors.joining(", ")));
        });
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        apiDescription.getRequestEvent(req.getPathInfo(), req.getMethod()).ifPresent(h -> handleRequest(req, resp, h));
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp, RequestEvent requestEvent) {
        try {
            Object result = lambdaMethodInvoker.invokeRequest(req, requestEvent);
            sendResponse(resp, getStatusCode(result), getBody(result));
        } catch (Exception e) {
            logger.error("Error handling request.", e);
            sendErrorResponse(resp, e);
        }
    }

    private String getBody(Object result) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return (String) result.getClass().getMethod(GET_BODY).invoke(result);
    }

    private Integer getStatusCode(Object result) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return (Integer) result.getClass().getMethod(GET_STATUS_CODE).invoke(result);
    }

    private void sendResponse(HttpServletResponse resp, Integer statusCode, String body) throws IOException {
        resp.setStatus(statusCode);
        if (body != null) {
            Writer writer = resp.getWriter();
            writer.write(body);
            writer.flush();
        }
    }

    private void sendErrorResponse(HttpServletResponse resp, Exception e) {
        try {
            sendResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}
