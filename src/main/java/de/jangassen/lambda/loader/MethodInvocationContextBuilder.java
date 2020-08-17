package de.jangassen.lambda.loader;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import de.jangassen.lambda.api.ApiResource;
import de.jangassen.lambda.exception.LambdaInvocationException;

import java.lang.reflect.Method;

public class MethodInvocationContextBuilder {
    private final ClassLoaderFactory classLoaderFactory;
    private ApiResource apiResource;

    private MethodInvocationContextBuilder(ClassLoaderFactory classLoaderFactory) {
        this.classLoaderFactory = classLoaderFactory;
    }

    public static MethodInvocationContextBuilder start(ClassLoaderFactory classLoaderFactory) {
        return new MethodInvocationContextBuilder(classLoaderFactory);
    }

    public MethodInvocationContextBuilder withApiResource(ApiResource apiResource) {
        this.apiResource = apiResource;
        return this;
    }

    public MethodInvocationContext build() {
        try {
            try {
                return build(APIGatewayProxyRequestEvent.class);
            } catch (ReflectiveOperationException e) {
                return build(AwsProxyRequest.class);
            }
        } catch (ReflectiveOperationException e) {
            throw new LambdaInvocationException(e);
        }
    }

    private MethodInvocationContext build(Class<?> parameterClass) throws ClassNotFoundException, NoSuchMethodException {
        ClassLoader classLoader = getClassLoader(apiResource);
        Thread.currentThread().setContextClassLoader(classLoader);

        Class<?> handlerClass = classLoader.loadClass(apiResource.getHandlerClass());
        Method handlerMethod = getHandlerMethod(apiResource, handlerClass, parameterClass);
        Object handlerInstance = getHandlerInstance(handlerClass);

        return new MethodInvocationContext(handlerMethod, handlerInstance, classLoader, parameterClass);
    }

    protected ClassLoader getClassLoader(ApiResource apiResource) {
        return classLoaderFactory.create(apiResource);
    }

    protected Object getHandlerInstance(Class<?> handlerClass) {
        try {
            return handlerClass.newInstance();
        } catch (Exception e) {
            throw new LambdaInvocationException(e);
        }
    }

    protected Method getHandlerMethod(ApiResource apiInvocation, Class<?> handlerClass, Class<?> parameterClass) throws NoSuchMethodException, ClassNotFoundException {
        Class<?> eventClass = handlerClass.getClassLoader().loadClass(parameterClass.getName());
        Class<?> proxyClass = handlerClass.getClassLoader().loadClass(Context.class.getName());

        return handlerClass.getMethod(apiInvocation.getHandlerMethod(), eventClass, proxyClass);
    }
}
