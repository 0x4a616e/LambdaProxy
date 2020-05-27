package de.jangassen.lambda.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import de.jangassen.lambda.api.RequestEvent;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class APIGatewayProxyRequestEventBuilder {
    private HttpServletRequest servletRequest;
    private RequestEvent requestEvent;

    private APIGatewayProxyRequestEventBuilder() {
    }

    public static APIGatewayProxyRequestEventBuilder start() {
        return new APIGatewayProxyRequestEventBuilder();
    }

    public APIGatewayProxyRequestEventBuilder withRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
        return this;
    }

    public APIGatewayProxyRequestEventBuilder withEvent(RequestEvent requestEvent) {
        this.requestEvent = requestEvent;
        return this;
    }

    public APIGatewayProxyRequestEvent build() throws IOException {
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setHeaders(getHeaders());
        event.setResource(requestEvent.getPath());
        event.setHttpMethod(servletRequest.getMethod());
        if (requestEvent.getPathMatchInfo() != null) {
            event.setPathParameters(requestEvent.getPathMatchInfo().getUriVariables());
        }
        event.setBody(getRequestEntity());
        event.setQueryStringParameters(getQueryParameters());
        return event;
    }

    private String getRequestEntity() throws IOException {
        return servletRequest.getReader().lines().collect(Collectors.joining());
    }

    private Map<String, String> getHeaders() {
        Map<String, String> header = new HashMap<>();
        Enumeration<String> headerNames = servletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            header.put(headerName, servletRequest.getHeader(headerName));
        }
        return header;
    }

    private Map<String, String> getQueryParameters() {
        if (StringUtils.isBlank(servletRequest.getQueryString())) {
            return Collections.emptyMap();
        }

        return Arrays.stream(StringUtils.split(servletRequest.getQueryString(), "&"))
                .map(v -> StringUtils.split(v, "=", 2))
                .collect(Collectors.toMap(v -> getDecodedParameterComponent(v, 0), v -> getDecodedParameterComponent(v, 1)));
    }

    private String getDecodedParameterComponent(String[] s, int index) {
        return Arrays.stream(s).skip(index).map(this::urlDecode).findFirst().orElse("");
    }

    private String urlDecode(String v) {
        try {
            return URLDecoder.decode(v, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
