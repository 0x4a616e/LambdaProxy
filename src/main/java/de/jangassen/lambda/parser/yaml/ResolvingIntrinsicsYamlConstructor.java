package de.jangassen.lambda.parser.yaml;

import de.jangassen.lambda.parser.intrinsics.Intrinsic;
import org.yaml.snakeyaml.nodes.Node;

import java.util.Collections;
import java.util.Map;

public class ResolvingIntrinsicsYamlConstructor extends IntrinsicsYamlConstructor {

    private final Map<String, Intrinsic> intrinsics;

    public ResolvingIntrinsicsYamlConstructor(Class<?> clazz, Map<String, Intrinsic> intrinsics) {
        super(clazz);
        this.intrinsics = intrinsics;
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

            Intrinsic intrinsic = intrinsics.get(key);
            if (intrinsic != null) {
                return intrinsic.apply(value);
            }

            String prefix = attachFnPrefix ? "Fn::" : "";
            return Collections.singletonMap(prefix + key, value);
        }

    }
}