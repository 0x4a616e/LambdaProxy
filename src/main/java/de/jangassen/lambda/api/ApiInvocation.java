package de.jangassen.lambda.api;

import de.jangassen.lambda.util.Generated;
import org.springframework.web.util.pattern.PathPattern;

import java.util.StringJoiner;

public class ApiInvocation extends ApiResource {

    private final String path;
    private final String resource;
    private final PathPattern.PathMatchInfo pathMatchInfo;

    public ApiInvocation(String resourceName, String codeUri, String handler, String path, String resource, PathPattern.PathMatchInfo pathMatchInfo) {
        super(resourceName, codeUri, handler);
        this.path = path;
        this.resource = resource;
        this.pathMatchInfo = pathMatchInfo;
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

    @Override
    @Generated
    public String toString() {
        return new StringJoiner(", ", ApiInvocation.class.getSimpleName() + "[", "]")
                .add("path='" + path + "'")
                .add("resource='" + resource + "'")
                .add("pathMatchInfo=" + pathMatchInfo)
                .add("resourceName='" + resourceName + "'")
                .add("codeUri='" + codeUri + "'")
                .add("handler='" + handler + "'")
                .toString();
    }
}
