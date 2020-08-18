package de.jangassen.lambda.api;

import de.jangassen.lambda.util.Generated;

import java.util.StringJoiner;

public class ApiMethod extends ApiResource {
    private final String pathPattern;
    private final String method;

    public ApiMethod(String resourceName, String codeUri, String handler, String pathPattern, String method) {
        super(resourceName, codeUri, handler);
        this.pathPattern = pathPattern;
        this.method = method;
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
    @Generated
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
