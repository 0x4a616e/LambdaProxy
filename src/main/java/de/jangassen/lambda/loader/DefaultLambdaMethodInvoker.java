package de.jangassen.lambda.loader;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import de.jangassen.lambda.api.RequestEvent;
import de.jangassen.lambda.lambda.APIGatewayProxyRequestEventBuilder;
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

    public Object invokeRequest(HttpServletRequest req, RequestEvent requestEvent) throws ReflectiveOperationException, IOException {
        MethodInvocationContext methodInvocationContext = getMethodInvocationContext(requestEvent);
        APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent = getAPIGatewayProxyRequestEvent(req, requestEvent);
        LambdaProxyContext lambdaProxyContext = getContext(requestEvent);

        return invoke(methodInvocationContext, apiGatewayProxyRequestEvent, lambdaProxyContext);
    }

    private Object invoke(MethodInvocationContext methodInvocationContext, APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, LambdaProxyContext lambdaProxyContext) throws IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
        return invokeUsingClassLoader(methodInvocationContext, lambdaProxyContext, apiGatewayProxyRequestEvent, methodInvocationContext.getClassLoader());
    }

    private LambdaProxyContext getContext(RequestEvent requestEvent) {
        return new LambdaProxyContext(requestEvent.getResourceName());
    }

    private APIGatewayProxyRequestEvent getAPIGatewayProxyRequestEvent(HttpServletRequest req, RequestEvent requestEvent) throws IOException {
        return APIGatewayProxyRequestEventBuilder.start()
                .withEvent(requestEvent).withRequest(req).build();
    }

    protected MethodInvocationContext getMethodInvocationContext(RequestEvent requestEvent) throws ClassNotFoundException {
        ClassLoader classLoader = getClassLoader(requestEvent);
        Thread.currentThread().setContextClassLoader(classLoader);

        Class<?> handlerClass = classLoader.loadClass(requestEvent.getHandlerClass());
        Method handlerMethod = getHandlerMethod(requestEvent, handlerClass);
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

    protected Method getHandlerMethod(RequestEvent requestEvent, Class<?> handlerClass) {
        try {
            Class<?> eventClass = handlerClass.getClassLoader().loadClass(APIGatewayProxyRequestEvent.class.getName());
            Class<?> proxyClass = handlerClass.getClassLoader().loadClass(Context.class.getName());

            return handlerClass.getMethod(requestEvent.getHandlerMethod(), eventClass, proxyClass);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private Object invokeUsingClassLoader(MethodInvocationContext invocationContext, LambdaProxyContext lambdaProxyContext, APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, ClassLoader classLoader) throws IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
        Object event = ClassLoaderUtils.moveToClassLoader(classLoader, apiGatewayProxyRequestEvent);
        Object context = ClassLoaderUtils.moveToClassLoader(classLoader, lambdaProxyContext);
        return invocationContext.getMethod().invoke(invocationContext.getInstance(), event, context);
    }
}
