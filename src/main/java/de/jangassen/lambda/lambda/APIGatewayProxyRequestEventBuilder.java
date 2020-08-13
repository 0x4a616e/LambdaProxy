package de.jangassen.lambda.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import de.jangassen.lambda.api.ApiInvocation;
import de.jangassen.lambda.util.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class APIGatewayProxyRequestEventBuilder {
    private HttpServletRequest servletRequest;
    private ApiInvocation apiInvocation;

    private APIGatewayProxyRequestEventBuilder() {
    }

    public static APIGatewayProxyRequestEventBuilder start() {
        return new APIGatewayProxyRequestEventBuilder();
    }

    public APIGatewayProxyRequestEventBuilder withRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
        return this;
    }

    public APIGatewayProxyRequestEventBuilder withEvent(ApiInvocation apiInvocation) {
        this.apiInvocation = apiInvocation;
        return this;
    }

    public APIGatewayProxyRequestEvent build() throws IOException {
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setHeaders(RequestUtils.getHeaders(servletRequest));
        event.setPath(apiInvocation.getPath());
        event.setResource(apiInvocation.getPath());
        event.setHttpMethod(servletRequest.getMethod());
        if (apiInvocation.getPathMatchInfo() != null) {
            event.setPathParameters(apiInvocation.getPathMatchInfo().getUriVariables());
        }
        event.setBody(RequestUtils.getRequestEntity(servletRequest));
        event.setQueryStringParameters(RequestUtils.getQueryParameters(servletRequest));
        return event;
    }
}
