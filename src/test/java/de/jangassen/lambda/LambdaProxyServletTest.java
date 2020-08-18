package de.jangassen.lambda;

import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import de.jangassen.lambda.api.ApiInvocation;
import de.jangassen.lambda.api.ApiMethod;
import de.jangassen.lambda.loader.LambdaMethodInvoker;
import de.jangassen.lambda.loader.MethodInvocationContextProvider;
import de.jangassen.lambda.parser.yaml.SamTemplate;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LambdaProxyServletTest {

    @Mock
    LambdaMethodInvoker lambdaMethodInvoker;

    @Mock
    MethodInvocationContextProvider methodInvocationContextProvider;

    @Mock
    HttpServletRequest req;

    @Mock
    HttpServletResponse res;

    @Test
    void testLambdaProxyServlet() throws IOException, ReflectiveOperationException {
        StringWriter responseWriter = new StringWriter();
        AwsProxyResponse response = new AwsProxyResponse();
        response.setBody("response");
        response.setStatusCode(418);

        when(req.getPathInfo()).thenReturn("/path/123");
        when(req.getMethod()).thenReturn(HttpMethod.GET);
        when(res.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(lambdaMethodInvoker.invokeRequest(any(HttpServletRequest.class), any(ApiInvocation.class), eq(null))).thenReturn(response);

        ApiMethod apiMethod = new ApiMethod("resource", "codeUri", "handler", "/path/{id}", HttpMethod.GET);

        SamTemplate.Cors cors = new SamTemplate.Cors();
        cors.AllowHeaders = "*";
        cors.AllowMethods = "GET";

        List<ApiMethod> apiMethods = Collections.singletonList(apiMethod);

        LambdaProxyServlet lambdaProxyServlet = new LambdaProxyServlet(lambdaMethodInvoker, methodInvocationContextProvider, cors, apiMethods);
        lambdaProxyServlet.service(req, res);

        verify(res, times(1)).addHeader(eq("Access-Control-Allow-Headers"), eq("*"));
        verify(res, times(1)).addHeader(eq("Access-Control-Allow-Methods"), eq("GET"));
        verify(res, times(1)).setStatus(418);

        assertEquals("response", responseWriter.getBuffer().toString());
    }

    @Test
    void testLambdaProxyServletCors() {
        when(req.getPathInfo()).thenReturn("/path/123");
        when(req.getMethod()).thenReturn(HttpMethod.OPTIONS);

        ApiMethod apiMethod = new ApiMethod("resource", "codeUri", "handler", "/path/{id}", HttpMethod.GET);

        SamTemplate.Cors cors = new SamTemplate.Cors();
        cors.AllowHeaders = "*";
        cors.AllowMethods = "GET";

        List<ApiMethod> apiMethods = Collections.singletonList(apiMethod);

        LambdaProxyServlet lambdaProxyServlet = new LambdaProxyServlet(lambdaMethodInvoker, methodInvocationContextProvider, cors, apiMethods);
        lambdaProxyServlet.service(req, res);

        verify(res, times(1)).addHeader(eq("Access-Control-Allow-Headers"), eq("*"));
        verify(res, times(1)).addHeader(eq("Access-Control-Allow-Methods"), eq("GET"));
        verify(res, times(1)).setStatus(HttpStatus.SC_OK);
    }
}