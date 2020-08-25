package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.ApiInvocation;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface LambdaMethodInvoker {
    Object invokeRequest(HttpServletRequest req, ApiInvocation apiInvocation, MethodInvocationContext methodInvocationContext) throws IOException;
}
