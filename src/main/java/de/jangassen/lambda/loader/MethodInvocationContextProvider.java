package de.jangassen.lambda.loader;

import de.jangassen.lambda.api.ApiResource;

public interface MethodInvocationContextProvider {
    MethodInvocationContext getMethodInvocationContext(ApiResource apiResource);
}
