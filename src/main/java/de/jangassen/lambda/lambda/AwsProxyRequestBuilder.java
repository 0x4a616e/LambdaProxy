package de.jangassen.lambda.lambda;

import com.amazonaws.serverless.proxy.model.*;
import de.jangassen.lambda.api.RequestEvent;
import de.jangassen.lambda.util.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;

public class AwsProxyRequestBuilder {
    private HttpServletRequest servletRequest;
    private RequestEvent requestEvent;

    private AwsProxyRequestBuilder() {
    }

    public static AwsProxyRequestBuilder start() {
        return new AwsProxyRequestBuilder();
    }

    public AwsProxyRequestBuilder withRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
        return this;
    }

    public AwsProxyRequestBuilder withEvent(RequestEvent requestEvent) {
        this.requestEvent = requestEvent;
        return this;
    }

    public AwsProxyRequest build() throws IOException {
        AwsProxyRequest event = new AwsProxyRequest();
        event.setMultiValueHeaders(toMultivaluedTreeMap(RequestUtils.getHeaders(servletRequest), Headers::new));
        event.setPath(requestEvent.getPath());
        event.setResource(requestEvent.getPath());
        event.setHttpMethod(servletRequest.getMethod());
        if (requestEvent.getPathMatchInfo() != null) {
            event.setPathParameters(requestEvent.getPathMatchInfo().getUriVariables());
        }
        event.setBody(RequestUtils.getRequestEntity(servletRequest));
        event.setMultiValueQueryStringParameters(toMultivaluedTreeMap(RequestUtils.getQueryParameters(servletRequest)));

        AwsProxyRequestContext requestContext = new AwsProxyRequestContext();
        requestContext.setIdentity(new ApiGatewayRequestIdentity());
        event.setRequestContext(requestContext);
        return event;
    }

    public static <K, V> MultiValuedTreeMap<K, V> toMultivaluedTreeMap(Map<K, V> map) {
        return toMultivaluedTreeMap(map, MultiValuedTreeMap::new);
    }

    public static <K, V, R extends MultiValuedTreeMap<K, V>> R toMultivaluedTreeMap(Map<K, V> map, Supplier<R> supplier) {
        R result = supplier.get();
        if (map != null) {
            map.forEach(result::add);
        }
        return result;
    }
}
