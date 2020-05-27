package de.jangassen.lambda.yaml;

import org.apache.commons.text.StringSubstitutor;
import org.yaml.snakeyaml.nodes.Node;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResolvingIntrinsicsYamlConstructor extends IntrinsicsYamlConstructor {
    private final Map<String, Object> preparsedObject;

    public ResolvingIntrinsicsYamlConstructor(Class<?> clazz, Map<String, Object> preparsedObject) {
        super(clazz);
        this.preparsedObject = preparsedObject;
    }

    @Override
    protected IntrinsicsYamlConstructor.ConstructFunction getConstructFunction(boolean attachFnPrefix, boolean forceSequenceValue) {
        return new ResolvingConstructFunction(attachFnPrefix, forceSequenceValue);
    }

    private class ResolvingConstructFunction extends IntrinsicsYamlConstructor.ConstructFunction {
        public ResolvingConstructFunction(boolean attachFnPrefix, boolean forceSequenceValue) {
            super(attachFnPrefix, forceSequenceValue);
        }

        public Object construct(Node node) {
            String key = node.getTag().getValue().substring(1);
            Object value = constructIntrinsicValueObject(node);

            if (value instanceof String) {
                if ("Ref".equals(key)) {
                    return getParameters().get(value);
                } else if ("Sub".equals(key)) {
                    return new StringSubstitutor(getParameters(), "${", "}").replace(value);
                }
            }

            String prefix = attachFnPrefix ? "Fn::" : "";
            return Collections.singletonMap(prefix + key, value);
        }

        private Map<String, String> getParameters() {
            Map<String, String> result = new HashMap<>();

            Object parameters = preparsedObject.get("Parameters");
            if (parameters instanceof Map) {
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) parameters).entrySet()) {
                    String name = (String) entry.getKey();

                    if (entry.getValue() instanceof Map) {
                        Object value = ((Map<?, ?>) entry.getValue()).get("Default");
                        if (value instanceof String) {
                            result.put(name, getParameterValue(name, (String) value));
                        }
                    }
                }
            }

            return result;
        }

        private String getParameterValue(String parameterName, String defaultValue) {
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
}