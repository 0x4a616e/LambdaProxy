package de.jangassen.lambda.lambda;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LambdaProxyContextTest {
    
    @Test
    void testLambdaProxyContext() {
        String function = "function";
        LambdaProxyContext context = new LambdaProxyContext(function);
        assertNotNull(context.getAwsRequestId());
        assertNull(context.getLogGroupName());
        assertNull(context.getLogStreamName());
        assertEquals(function, context.getFunctionName());
        assertNull(context.getFunctionVersion());
        assertNull(context.getInvokedFunctionArn());
        assertNull(context.getIdentity());
        assertNull(context.getClientContext());
        assertEquals(0, context.getRemainingTimeInMillis());
        assertEquals(0, context.getMemoryLimitInMB());
        assertNull(context.getLogger());
    }
}