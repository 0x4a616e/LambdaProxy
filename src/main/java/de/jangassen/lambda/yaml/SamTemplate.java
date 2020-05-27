package de.jangassen.lambda.yaml;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class SamTemplate {
    public String Transform;
    public String AWSTemplateFormatVersion;
    public String Description;
    public Globals Globals;
    public Map<String, Map<String, String>> Parameters;
    public Map<String, Resource> Resources;
    public List<String> Policies;

    public static class Globals {
        public Function Function;
    }

    public static class Function {
        public Integer Timeout;
    }

    public static class Resource {
        public String Type;
        public ResourceProperties Properties;
    }

    public static class ResourceProperties {
        public String CodeUri;
        public List<String> Policies;
        public String Handler;
        public String Runtime;
        public Environment Environment;
        public Map<String, Event> Events;
        public Map<String, Object> DefinitionBody;
    }

    public static class EventProperties {
        public String Path;
        public String Method;
    }

    public static class Event {
        public String Type;
        public EventProperties Properties;
    }

    public static class Environment {
        public Map<String, Object> Variables;
    }
}

