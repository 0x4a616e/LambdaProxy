package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.ApiInvocation;
import de.jangassen.lambda.api.ApiResource;
import de.jangassen.lambda.util.TestClass;
import org.apache.catalina.connector.Request;
import org.apache.coyote.http11.filters.VoidInputFilter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultLambdaMethodInvokerTest {
    @Test
    public void testSimpleInvocationInSameClassLoader() throws IOException, ReflectiveOperationException {
        MethodInvocationContextCache methodInvocationContextCache = new MethodInvocationContextCache(new TestClassLoaderFactory(), Duration.ofSeconds(10));
        DefaultLambdaMethodInvoker defaultLambdaMethodInvoker = new DefaultLambdaMethodInvoker();

        Request req = createTestRequest();
        ApiInvocation apiInvocation = new ApiInvocation("test", "test", TestClass.class.getName() + "::handleRequest", "resoure", null, null);
        Object result = defaultLambdaMethodInvoker.invokeRequest(req, apiInvocation, methodInvocationContextCache.getMethodInvocationContext(apiInvocation));

        assertEquals(TestClass.SUCCESS, result);
    }

    private Request createTestRequest() {
        Request req = new Request();
        org.apache.coyote.Request coyoteRequest = new org.apache.coyote.Request();
        coyoteRequest.setInputBuffer(new VoidInputFilter());
        req.setCoyoteRequest(coyoteRequest);
        return req;
    }

    private static class TestClassLoaderFactory implements ClassLoaderFactory {
        @Override
        public ClassLoader create(ApiResource handler) {
            return DefaultLambdaMethodInvokerTest.class.getClassLoader();
        }

        @Override
        public void close(ClassLoader classLoader) {
            // Nothing to do here
        }
    }
}