package de.jangassen.lambda.lambda;

import com.amazonaws.serverless.proxy.model.*;
import de.jangassen.lambda.api.ApiInvocation;
import de.jangassen.lambda.util.RequestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;

public class AwsProxyRequestBuilder {
    private HttpServletRequest servletRequest;
    private ApiInvocation apiInvocation;

    private AwsProxyRequestBuilder() {
    }

    public static AwsProxyRequestBuilder start() {
        return new AwsProxyRequestBuilder();
    }

    public AwsProxyRequestBuilder withRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
        return this;
    }

    public AwsProxyRequestBuilder withEvent(ApiInvocation apiInvocation) {
        this.apiInvocation = apiInvocation;
        return this;
    }

    public AwsProxyRequest build() throws IOException {
        AwsProxyRequest event = new AwsProxyRequest();
        event.setMultiValueHeaders(toMultivaluedTreeMap(RequestUtils.getHeaders(servletRequest), Headers::new));
        event.setPath(apiInvocation.getPath());
        event.setResource(apiInvocation.getPath());
        event.setHttpMethod(servletRequest.getMethod());
        if (apiInvocation.getPathMatchInfo() != null) {
            event.setPathParameters(apiInvocation.getPathMatchInfo().getUriVariables());
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
