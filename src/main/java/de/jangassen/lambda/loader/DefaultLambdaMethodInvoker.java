package de.jangassen.lambda.loader;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import de.jangassen.lambda.api.ApiInvocation;
import de.jangassen.lambda.exception.LambdaInvocationException;
import de.jangassen.lambda.lambda.APIGatewayProxyRequestEventBuilder;
import de.jangassen.lambda.lambda.AwsProxyRequestBuilder;
import de.jangassen.lambda.lambda.LambdaProxyContext;
import de.jangassen.lambda.util.ClassLoaderUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;

public class DefaultLambdaMethodInvoker implements LambdaMethodInvoker {

    @Override
    public Object invokeRequest(HttpServletRequest req, ApiInvocation apiInvocation, MethodInvocationContext methodInvocationContext) throws IOException {
        if (APIGatewayProxyRequestEvent.class.equals(methodInvocationContext.getParameterClass())) {
            return invoke(apiInvocation, getAPIGatewayProxyRequestEvent(req, apiInvocation), methodInvocationContext);
        } else {
            return invoke(apiInvocation, getAwsProxyRequest(req, apiInvocation), methodInvocationContext);
        }
    }

    private Object invoke(ApiInvocation apiInvocation, Object request, MethodInvocationContext methodInvocationContext) {
        LambdaProxyContext context = getContext(apiInvocation);

        return invokeUsingClassLoader(methodInvocationContext, context, request, methodInvocationContext.getClassLoader());
    }

    private LambdaProxyContext getContext(ApiInvocation apiInvocation) {
        return new LambdaProxyContext(apiInvocation.getResourceName());
    }

    private APIGatewayProxyRequestEvent getAPIGatewayProxyRequestEvent(HttpServletRequest req, ApiInvocation apiInvocation) throws IOException {
        return APIGatewayProxyRequestEventBuilder.start()
                .withEvent(apiInvocation)
                .withRequest(req)
                .build();
    }

    private AwsProxyRequest getAwsProxyRequest(HttpServletRequest req, ApiInvocation apiInvocation) throws IOException {
        return AwsProxyRequestBuilder.start()
                .withEvent(apiInvocation)
                .withRequest(req)
                .build();
    }

    private Object invokeUsingClassLoader(MethodInvocationContext invocationContext, LambdaProxyContext lambdaProxyContext, Object requestEvent, ClassLoader classLoader) {
        Object event = ClassLoaderUtils.moveToClassLoader(classLoader, requestEvent);
        Object context = ClassLoaderUtils.moveToClassLoader(classLoader, lambdaProxyContext);
        Thread.currentThread().setContextClassLoader(classLoader);

        return invocationContext.getInvocationStrategy().invoke(
                () -> invoke(event, context, invocationContext.getInstance(), invocationContext.getMethod()));
    }

    private Object invoke(Object event, Object context, Object instance, Method method) {
        try {
            return method.invoke(instance, event, context);
        } catch (Exception e) {
            throw new LambdaInvocationException(e);
        }
    }
}
