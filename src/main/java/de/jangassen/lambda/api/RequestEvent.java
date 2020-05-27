package de.jangassen.lambda.api;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.pattern.PathPattern;

public class RequestEvent {

    public static final String SEPARATOR_CHARS = "::";

    private final String resourceName;
    private final String codeUri;
    private final String handler;
    private final String path;
    private final PathPattern.PathMatchInfo pathMatchInfo;

    public RequestEvent(String resourceName, String codeUri, String handler, String path, PathPattern.PathMatchInfo pathMatchInfo) {
        this.resourceName = resourceName;
        this.codeUri = codeUri;
        this.handler = handler;
        this.path = path;
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

    public String getResourceName() {
        return resourceName;
    }

    public String getCodeUri() {
        return codeUri;
    }
}
