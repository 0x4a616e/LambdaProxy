package de.jangassen.lambda.util;

import de.jangassen.lambda.parser.yaml.SamTemplate;
import org.apache.commons.text.StringSubstitutor;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class ParameterUtils {

    private ParameterUtils() {
    }

    public static Map<String, String> getParameters(SamTemplate samTemplate) {
        return getParameters(samTemplate.Parameters);
    }

    public static String resolve(String string, SamTemplate samTemplate) {
        return new StringSubstitutor(getParameters(samTemplate), "${", "}").replace(string);
    }

    public static String resolve(String string, Map<String, String> parameters) {
        return new StringSubstitutor(parameters, "${", "}").replace(string);
    }

    public static Map<String, String> getParameters(Map<?, ?> parameters) {
        return parameters.entrySet().stream()
                .filter(e -> e.getKey() instanceof String && e.getValue() instanceof Map)
                .map(ParameterUtils::getParameterNameToValue)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static AbstractMap.SimpleImmutableEntry<String, String> getParameterNameToValue(Map.Entry<?, ?> e) {
        return new AbstractMap.SimpleImmutableEntry<>((String) e.getKey(), getParameterValue((String) e.getKey(), (Map<?, ?>) e.getValue()));
    }

    private static String getParameterValue(String parameterName, Map<?, ?> parameter) {
        Object value = parameter.get("Default");
        String defaultValue = value instanceof String ? (String) value : null;
        return getParameterValue(parameterName, defaultValue);
    }

    public static String getParameterValue(String parameterName, String defaultValue) {
        String property = System.getProperty(parameterName);
        if (property != null) {
            return property;
        }

        String env = System.getenv(parameterName);
        if (env != null) {
            return env;
        }

        if (defaultValue != null) {
            return defaultValue;
        }
        return parameterName;
    }
}
