package de.jangassen.lambda.parser.intrinsics;

import de.jangassen.lambda.exception.InvalidIntrinsicValue;

import java.util.List;
import java.util.stream.Collectors;

public class Join implements Intrinsic {

    public static final String INTRINSIC_NAME = "Join";

    @Override
    public Object apply(Object obj) {
        if (obj instanceof List && ((List<?>) obj).size() == 2) {
            String separator = String.valueOf(((List<?>) obj).get(0));
            Object values = ((List<?>) obj).get(1);

            if (values instanceof List) {
                return ((List<?>) values).stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(separator));
            }
        }

        throw new InvalidIntrinsicValue(String.valueOf(obj));
    }

    @Override
    public String name() {
        return INTRINSIC_NAME;
    }
}
