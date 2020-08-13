package de.jangassen.lambda.loader;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import de.jangassen.lambda.api.RequestEvent;
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
    public Object invokeRequest(HttpServletRequest req, RequestEvent requestEvent) throws ReflectiveOperationException, IOException {
        try {
            return invoke(requestEvent, getAPIGatewayProxyRequestEvent(req, requestEvent));
        } catch (NoSuchMethodException e) {
            return invoke(requestEvent, getAwsProxyRequest(req, requestEvent));
        }
    }

    private Object invoke(RequestEvent requestEvent, Object request) throws ReflectiveOperationException {
        MethodInvocationContext methodInvocationContext = getMethodInvocationContext(requestEvent, request.getClass());
        LambdaProxyContext context = getContext(requestEvent);

        return invokeUsingClassLoader(methodInvocationContext, context, request, methodInvocationContext.getClassLoader());
    }

    private LambdaProxyContext getContext(RequestEvent requestEvent) {
        return new LambdaProxyContext(requestEvent.getResourceName());
    }

    private APIGatewayProxyRequestEvent getAPIGatewayProxyRequestEvent(HttpServletRequest req, RequestEvent requestEvent) throws IOException {
        return APIGatewayProxyRequestEventBuilder.start()
                .withEvent(requestEvent)
                .withRequest(req)
                .build();
    }

    private AwsProxyRequest getAwsProxyRequest(HttpServletRequest req, RequestEvent requestEvent) throws IOException {
        return AwsProxyRequestBuilder.start()
                .withEvent(requestEvent)
                .withRequest(req)
                .build();
    }

    protected MethodInvocationContext getMethodInvocationContext(RequestEvent requestEvent, Class<?> parameterClass) throws ClassNotFoundException {
        ClassLoader classLoader = getClassLoader(requestEvent);
        Thread.currentThread().setContextClassLoader(classLoader);

        Class<?> handlerClass = classLoader.loadClass(requestEvent.getHandlerClass());
        Method handlerMethod = getHandlerMethod(requestEvent, handlerClass, parameterClass);
        Object handlerInstance = getHandlerInstance(handlerClass);

        return new MethodInvocationContext(handlerMethod, handlerInstance, classLoader);
    }

    protected ClassLoader getClassLoader(RequestEvent requestEvent) {
        return classLoaderFactory.create(requestEvent);
    }

    protected Object getHandlerInstance(Class<?> handlerClass) {
        try {
            return handlerClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Method getHandlerMethod(RequestEvent requestEvent, Class<?> handlerClass, Class<?> parameterClass) {
        try {
            Class<?> eventClass = handlerClass.getClassLoader().loadClass(parameterClass.getName());
            Class<?> proxyClass = handlerClass.getClassLoader().loadClass(Context.class.getName());

            return handlerClass.getMethod(requestEvent.getHandlerMethod(), eventClass, proxyClass);
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
