package de.jangassen.lambda.parser.intrinsics;

public interface Intrinsic {
    Object apply(Object obj);

    String name();
}
