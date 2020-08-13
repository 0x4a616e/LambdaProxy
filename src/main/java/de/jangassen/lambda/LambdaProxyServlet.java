package de.jangassen.lambda;

import de.jangassen.lambda.api.ApiInvocation;
import de.jangassen.lambda.api.ApiMethod;
import de.jangassen.lambda.loader.LambdaMethodInvoker;
import de.jangassen.lambda.util.ApiMethodUtils;
import de.jangassen.lambda.yaml.SamTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

class LambdaProxyServlet extends HttpServlet {

    public static final String GET_BODY = "getBody";
    public static final String GET_STATUS_CODE = "getStatusCode";

    private final Logger logger = LoggerFactory.getLogger(LambdaProxyServlet.class);

    private final LambdaMethodInvoker lambdaMethodInvoker;
    private final List<ApiMethod> apiMethods;
    private final SamTemplate.Cors cors;

    public LambdaProxyServlet(LambdaMethodInvoker lambdaMethodInvoker, SamTemplate.Cors cors, List<ApiMethod> apiMethods) {
        this.cors = cors;
        this.lambdaMethodInvoker = lambdaMethodInvoker;
        this.apiMethods = apiMethods;

        apiMethods.forEach(e -> logger.info("* {}", e));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        Optional<ApiInvocation> requestEvent = ApiMethodUtils.getRequestEvent(apiMethods, req.getPathInfo(), req.getMethod());
        if (!requestEvent.isPresent() && cors != null && StringUtils.equalsIgnoreCase(req.getMethod(), HttpMethod.OPTIONS)) {
            addCorsHeaders(resp);
            resp.setStatus(HttpStatus.SC_OK);
        } else {
            requestEvent.ifPresent(event -> handleRequest(req, resp, event));
        }
    }

    private void addCorsHeaders(HttpServletResponse resp) {
        if (cors != null) {
            if (cors.AllowHeaders != null) {
                resp.addHeader("Access-Control-Allow-Headers", getCorsHeaderValue(cors.AllowHeaders));
            }
            if (cors.AllowMethods != null) {
                resp.addHeader("Access-Control-Allow-Methods", getCorsHeaderValue(cors.AllowMethods));
            }
            if (cors.AllowOrigin != null) {
                resp.addHeader("Access-Control-Allow-Origin", getCorsHeaderValue(cors.AllowOrigin));
            }
        }
    }

    private String getCorsHeaderValue(String headerValue) {
        return StringUtils.strip(headerValue, "'");
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp, ApiInvocation apiInvocation) {
        try {
            Object result = lambdaMethodInvoker.invokeRequest(req, apiInvocation);
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
        addCorsHeaders(resp);
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
