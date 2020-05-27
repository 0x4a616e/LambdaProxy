package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.RequestEvent;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface LambdaMethodInvoker {
    Object invokeRequest(HttpServletRequest req, RequestEvent requestEvent) throws ReflectiveOperationException, IOException;
}
