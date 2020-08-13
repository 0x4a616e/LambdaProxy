package de.jangassen.lambda.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.pattern.PathPattern;

public class ApiInvocation {

    public static final String SEPARATOR_CHARS = "::";

    private final String resourceName;
    private final String codeUri;
    private final String handler;
    private final String path;
    private final String resource;
    private final PathPattern.PathMatchInfo pathMatchInfo;

    public ApiInvocation(String resourceName, String codeUri, String handler, String path, String resource, PathPattern.PathMatchInfo pathMatchInfo) {
        this.resourceName = resourceName;
        this.codeUri = codeUri;
        this.handler = handler;
        this.path = path;
        this.resource = resource;
        this.pathMatchInfo = pathMatchInfo;
    }

    public String getHandlerClass() {
        return StringUtils.split(handler, SEPARATOR_CHARS)[0];
    }

    public String getHandlerMethod() {
        return StringUtils.split(handler, SEPARATOR_CHARS)[1];
    }

    public PathPattern.PathMatchInfo getPathMatchInfo() {
        return pathMatchInfo;
    }

    public String getPath() {
        return path;
    }

    public String getResource() {
        return resource;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getCodeUri() {
        return codeUri;
    }
}
