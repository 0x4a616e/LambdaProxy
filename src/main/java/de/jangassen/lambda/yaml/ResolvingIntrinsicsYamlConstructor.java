package de.jangassen.lambda.yaml;

import de.jangassen.lambda.util.ParameterUtils;
import org.yaml.snakeyaml.nodes.Node;

import java.util.Collections;
import java.util.Map;

public class ResolvingIntrinsicsYamlConstructor extends IntrinsicsYamlConstructor {

    private final Map<String, String> parameters;

    public ResolvingIntrinsicsYamlConstructor(Class<?> clazz, Map<String, Object> preparsedObject) {
        super(clazz);
        parameters = getParameters(preparsedObject);
    }

    private Map<String, String> getParameters(Map<String, Object> preparsedObject) {
        Object parameters = preparsedObject.get("Parameters");
        if (parameters instanceof Map) {
            return ParameterUtils.getParameters((Map<?, ?>) parameters);
        }

        return Collections.emptyMap();
    }

    @Override
    protected IntrinsicsYamlConstructor.ConstructFunction getConstructFunction(boolean attachFnPrefix, boolean forceSequenceValue) {
        return new ResolvingConstructFunction(attachFnPrefix, forceSequenceValue);
    }

    private class ResolvingConstructFunction extends IntrinsicsYamlConstructor.ConstructFunction {
        public ResolvingConstructFunction(boolean attachFnPrefix, boolean forceSequenceValue) {
            super(attachFnPrefix, forceSequenceValue);
        }

        @Override
        public Object construct(Node node) {
            String key = node.getTag().getValue().substring(1);
            Object value = constructIntrinsicValueObject(node);

            if (value instanceof String) {
                if ("Ref".equals(key)) {
                    return parameters.get(value);
                } else if ("Sub".equals(key)) {
                    return ParameterUtils.resolve((String) value, parameters);
                }
            }

            String prefix = attachFnPrefix ? "Fn::" : "";
            return Collections.singletonMap(prefix + key, value);
        }

    }
}