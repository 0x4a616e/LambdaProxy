package de.jangassen.lambda.parser.intrinsics;

import java.util.Map;

public class Ref implements Intrinsic {

    public static final String NAME = "Ref";

    private final Map<String, String> parameters;

    public Ref(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String apply(Object obj) {
        if (obj instanceof String) {
            return parameters.get(obj);
        }
        return String.valueOf(obj);
    }

    @Override
    public String name() {
        return NAME;
    }
}
