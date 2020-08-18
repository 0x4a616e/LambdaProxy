package de.jangassen.lambda.lambda;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.MultiValuedTreeMap;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AwsProxyRequestBuilderTest {

    @Mock
    HttpServletRequest request;

    @Test
    void testAwsProxyRequestBuilder() throws IOException {
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.singleton("test")));
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("content")));
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(request.getQueryString()).thenReturn("v1=foo&v2=bar");

        ApiInvocation invocation = new ApiInvocation("name", "codeUri", "handler", "path", "resource", null);

        AwsProxyRequest build = AwsProxyRequestBuilder.start()
                .withEvent(invocation)
                .withRequest(request)
                .build();

        MultiValuedTreeMap<String, String> queryParams = new MultiValuedTreeMap<>();
        queryParams.add("v1", "foo");
        queryParams.add("v2", "bar");

        assertEquals("content", build.getBody());
        assertEquals(HttpMethod.POST, build.getHttpMethod());
        assertEquals("path", build.getPath());
        assertEquals("path", build.getResource());
        assertEquals(queryParams, build.getMultiValueQueryStringParameters());
        assertNotNull(build.getRequestContext());
    }

}