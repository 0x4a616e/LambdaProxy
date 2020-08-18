package de.jangassen.lambda.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import de.jangassen.lambda.api.ApiInvocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class APIGatewayProxyRequestEventBuilderTest {

    @Mock
    HttpServletRequest request;

    @Test
    void testAPIGatewayProxyRequestEventBuilder() throws IOException {
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.singleton("test")));
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("content")));
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(request.getQueryString()).thenReturn("v1=foo&v2=bar");

        ApiInvocation invocation = new ApiInvocation("name", "codeUri", "handler", "path", "resource", null);

        APIGatewayProxyRequestEvent build = APIGatewayProxyRequestEventBuilder.start()
                .withEvent(invocation)
                .withRequest(request)
                .build();

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("v1", "foo");
        queryParams.put("v2", "bar");

        assertEquals("content", build.getBody());
        assertEquals(HttpMethod.POST, build.getHttpMethod());
        assertEquals("path", build.getPath());
        assertEquals("path", build.getResource());
        assertEquals(queryParams, build.getQueryStringParameters());
    }

}