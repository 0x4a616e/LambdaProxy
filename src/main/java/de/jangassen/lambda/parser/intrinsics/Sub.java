package de.jangassen.lambda.parser.intrinsics;

import de.jangassen.lambda.util.ParameterUtils;

import java.util.Map;

public class Sub implements Intrinsic {

    public static final String INTRINSIC_NAME = "Sub";

    private final Map<String, String> parameters;

    public Sub(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String apply(Object obj) {
        if (obj instanceof String) {
            return ParameterUtils.resolve((String) obj, parameters);
        }
        return String.valueOf(obj);
    }

    @Override
    public String name() {
        return INTRINSIC_NAME;
    }
}
