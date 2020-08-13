package de.jangassen.lambda.loader;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import de.jangassen.lambda.api.ApiInvocation;
import de.jangassen.lambda.lambda.APIGatewayProxyRequestEventBuilder;
import de.jangassen.lambda.lambda.AwsProxyRequestBuilder;
import de.jangassen.lambda.lambda.LambdaProxyContext;
import de.jangassen.lambda.util.ClassLoaderUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DefaultLambdaMethodInvoker implements LambdaMethodInvoker {

    private final ClassLoaderFactory classLoaderFactory;

    public DefaultLambdaMethodInvoker(ClassLoaderFactory classLoaderFactory) {
        this.classLoaderFactory = classLoaderFactory;
    }

    @Override
    public Object invokeRequest(HttpServletRequest req, ApiInvocation apiInvocation) throws ReflectiveOperationException, IOException {
        try {
            return invoke(apiInvocation, getAPIGatewayProxyRequestEvent(req, apiInvocation));
        } catch (NoSuchMethodException e) {
            return invoke(apiInvocation, getAwsProxyRequest(req, apiInvocation));
        }
    }

    private Object invoke(ApiInvocation apiInvocation, Object request) throws ReflectiveOperationException {
        MethodInvocationContext methodInvocationContext = getMethodInvocationContext(apiInvocation, request.getClass());
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

    protected MethodInvocationContext getMethodInvocationContext(ApiInvocation apiInvocation, Class<?> parameterClass) throws ClassNotFoundException {
        ClassLoader classLoader = getClassLoader(apiInvocation);
        Thread.currentThread().setContextClassLoader(classLoader);

        Class<?> handlerClass = classLoader.loadClass(apiInvocation.getHandlerClass());
        Method handlerMethod = getHandlerMethod(apiInvocation, handlerClass, parameterClass);
        Object handlerInstance = getHandlerInstance(handlerClass);

        return new MethodInvocationContext(handlerMethod, handlerInstance, classLoader);
    }

    protected ClassLoader getClassLoader(ApiInvocation apiInvocation) {
        return classLoaderFactory.create(apiInvocation);
    }

    protected Object getHandlerInstance(Class<?> handlerClass) {
        try {
            return handlerClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Method getHandlerMethod(ApiInvocation apiInvocation, Class<?> handlerClass, Class<?> parameterClass) {
        try {
            Class<?> eventClass = handlerClass.getClassLoader().loadClass(parameterClass.getName());
            Class<?> proxyClass = handlerClass.getClassLoader().loadClass(Context.class.getName());

            return handlerClass.getMethod(apiInvocation.getHandlerMethod(), eventClass, proxyClass);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private Object invokeUsingClassLoader(MethodInvocationContext invocationContext, LambdaProxyContext lambdaProxyContext, Object requestEvent, ClassLoader classLoader) throws IllegalAccessException, InvocationTargetException {
        Object event = ClassLoaderUtils.moveToClassLoader(classLoader, requestEvent);
        Object context = ClassLoaderUtils.moveToClassLoader(classLoader, lambdaProxyContext);
        return invocationContext.getMethod().invoke(invocationContext.getInstance(), event, context);
    }
}
