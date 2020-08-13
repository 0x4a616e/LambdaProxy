package de.jangassen.lambda.api;

import org.apache.commons.lang3.StringUtils;

public class ApiResource {
    private static final String SEPARATOR_CHARS = "::";
    
    protected final String resourceName;
    protected final String codeUri;
    protected final String handler;

    public ApiResource(String resourceName, String codeUri, String handler) {
        this.resourceName = resourceName;
        this.codeUri = codeUri;
        this.handler = handler;
    }

    public String getHandlerClass() {
        return StringUtils.split(handler, SEPARATOR_CHARS)[0];
    }

    public String getHandlerMethod() {
        return StringUtils.split(handler, SEPARATOR_CHARS)[1];
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getCodeUri() {
        return codeUri;
    }
}
