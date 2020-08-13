package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.RequestEvent;
import de.jangassen.lambda.util.TestClass;
import org.apache.catalina.connector.Request;
import org.apache.coyote.http11.filters.VoidInputFilter;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultLambdaMethodInvokerTest {
    @Test
    public void testSimpleInvocationInSameClassLoader() throws IOException, ReflectiveOperationException {
        DefaultLambdaMethodInvoker defaultLambdaMethodInvoker = new DefaultLambdaMethodInvoker(h -> DefaultLambdaMethodInvokerTest.class.getClassLoader());

        Request req = createTestRequest();
        Object result = defaultLambdaMethodInvoker.invokeRequest(req, new RequestEvent("test", "test", TestClass.class.getName() + "::handleRequest", "resoure", null, null));

        assertEquals(TestClass.SUCCESS, result);
    }

    private Request createTestRequest() {
        Request req = new Request();
        org.apache.coyote.Request coyoteRequest = new org.apache.coyote.Request();
        coyoteRequest.setInputBuffer(new VoidInputFilter());
        req.setCoyoteRequest(coyoteRequest);
        return req;
    }
}