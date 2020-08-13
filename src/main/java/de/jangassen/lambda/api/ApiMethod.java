package de.jangassen.lambda.api;

import java.util.StringJoiner;

public class ApiMethod {
    private final String resourceName;
    private final String codeUri;
    private final String handler;
    private final String pathPattern;
    private final String method;

    public ApiMethod(String resourceName, String codeUri, String handler, String pathPattern, String method) {
        this.resourceName = resourceName;
        this.codeUri = codeUri;
        this.handler = handler;
        this.pathPattern = pathPattern;
        this.method = method;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getCodeUri() {
        return codeUri;
    }

    public String getHandler() {
        return handler;
    }

    public String getPathPattern() {
        return pathPattern;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ApiMethod.class.getSimpleName() + "[", "]")
                .add("resourceName='" + resourceName + "'")
                .add("codeUri='" + codeUri + "'")
                .add("handler='" + handler + "'")
                .add("pathPattern='" + pathPattern + "'")
                .add("method='" + method + "'")
                .toString();
    }
}
