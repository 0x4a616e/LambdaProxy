package de.jangassen.lambda.parser.yaml;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class SamTemplate {
    private String transform;
    private String aWSTemplateFormatVersion;
    private String description;
    private Globals globals;
    private Map<String, Map<String, String>> parameters;
    private Map<String, Resource> resources;
    private List<String> policies;

    public String getTransform() {
        return transform;
    }

    public void setTransform(String transform) {
        this.transform = transform;
    }

    public String getAWSTemplateFormatVersion() {
        return aWSTemplateFormatVersion;
    }

    public void setAWSTemplateFormatVersion(String AWSTemplateFormatVersion) {
        aWSTemplateFormatVersion = AWSTemplateFormatVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SamTemplate.Globals getGlobals() {
        return globals;
    }

    public void setGlobals(SamTemplate.Globals globals) {
        this.globals = globals;
    }

    public Map<String, Map<String, String>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Map<String, String>> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Resource> getResources() {
        return resources;
    }

    public void setResources(Map<String, Resource> resources) {
        this.resources = resources;
    }

    public List<String> getPolicies() {
        return policies;
    }

    public void setPolicies(List<String> policies) {
        this.policies = policies;
    }

    public static class Globals {
        private Function function;
        private Api api;

        public SamTemplate.Function getFunction() {
            return function;
        }

        public void setFunction(SamTemplate.Function function) {
            this.function = function;
        }

        public SamTemplate.Api getApi() {
            return api;
        }

        public void setApi(SamTemplate.Api api) {
            this.api = api;
        }
    }

    public static class Function {
        private Integer timeout;

        public Integer getTimeout() {
            return timeout;
        }

        public void setTimeout(Integer timeout) {
            this.timeout = timeout;
        }
    }

    public static class Resource {
        private String type;
        private ResourceProperties properties;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ResourceProperties getProperties() {
            return properties;
        }

        public void setProperties(ResourceProperties properties) {
            this.properties = properties;
        }
    }

    public static class ResourceProperties {
        private String codeUri;
        private List<String> policies;
        private String handler;
        private String runtime;
        private Environment environment;
        private Map<String, Event> events;
        private Map<String, Object> definitionBody;

        public String getCodeUri() {
            return codeUri;
        }

        public void setCodeUri(String codeUri) {
            this.codeUri = codeUri;
        }

        public List<String> getPolicies() {
            return policies;
        }

        public void setPolicies(List<String> policies) {
            this.policies = policies;
        }

        public String getHandler() {
            return handler;
        }

        public void setHandler(String handler) {
            this.handler = handler;
        }

        public String getRuntime() {
            return runtime;
        }

        public void setRuntime(String runtime) {
            this.runtime = runtime;
        }

        public SamTemplate.Environment getEnvironment() {
            return environment;
        }

        public void setEnvironment(SamTemplate.Environment environment) {
            this.environment = environment;
        }

        public Map<String, Event> getEvents() {
            return events;
        }

        public void setEvents(Map<String, Event> events) {
            this.events = events;
        }

        public Map<String, Object> getDefinitionBody() {
            return definitionBody;
        }

        public void setDefinitionBody(Map<String, Object> definitionBody) {
            this.definitionBody = definitionBody;
        }
    }

    public static class EventProperties {
        private String path;
        private String method;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }
    }

    public static class Event {
        private String type;
        private EventProperties properties;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public EventProperties getProperties() {
            return properties;
        }

        public void setProperties(EventProperties properties) {
            this.properties = properties;
        }
    }

    public static class Environment {
        private Map<String, Object> variables;

        public Map<String, Object> getVariables() {
            return variables;
        }

        public void setVariables(Map<String, Object> variables) {
            this.variables = variables;
        }
    }

    public static class Api {
        private Cors cors;

        public SamTemplate.Cors getCors() {
            return cors;
        }

        public void setCors(SamTemplate.Cors cors) {
            this.cors = cors;
        }
    }

    public static class Cors {
        private String allowMethods;
        private String allowHeaders;
        private String allowOrigin;

        public String getAllowMethods() {
            return allowMethods;
        }

        public void setAllowMethods(String allowMethods) {
            this.allowMethods = allowMethods;
        }

        public String getAllowHeaders() {
            return allowHeaders;
        }

        public void setAllowHeaders(String allowHeaders) {
            this.allowHeaders = allowHeaders;
        }

        public String getAllowOrigin() {
            return allowOrigin;
        }

        public void setAllowOrigin(String allowOrigin) {
            this.allowOrigin = allowOrigin;
        }
    }
}

