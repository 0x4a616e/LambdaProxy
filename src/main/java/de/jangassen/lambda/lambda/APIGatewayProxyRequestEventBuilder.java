package de.jangassen.lambda.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import de.jangassen.lambda.api.RequestEvent;
import de.jangassen.lambda.util.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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
        event.setHeaders(RequestUtils.getHeaders(servletRequest));
        event.setPath(requestEvent.getPath());
        event.setResource(requestEvent.getPath());
        event.setHttpMethod(servletRequest.getMethod());
        if (requestEvent.getPathMatchInfo() != null) {
            event.setPathParameters(requestEvent.getPathMatchInfo().getUriVariables());
        }
        event.setBody(RequestUtils.getRequestEntity(servletRequest));
        event.setQueryStringParameters(RequestUtils.getQueryParameters(servletRequest));
        return event;
    }
}
