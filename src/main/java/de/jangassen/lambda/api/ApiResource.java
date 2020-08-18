package de.jangassen.lambda.api;

import de.jangassen.lambda.util.Generated;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

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

    @Override
    @Generated
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiResource that = (ApiResource) o;
        return Objects.equals(resourceName, that.resourceName) &&
                Objects.equals(codeUri, that.codeUri) &&
                Objects.equals(handler, that.handler);
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(resourceName, codeUri, handler);
    }
}
