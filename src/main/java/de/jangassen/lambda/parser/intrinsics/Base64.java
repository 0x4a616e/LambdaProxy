package de.jangassen.lambda.parser.intrinsics;

import de.jangassen.lambda.exception.InvalidIntrinsicValue;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Base64 implements Intrinsic {

    public static final String INTRINSIC_NAME = "Base64";

    private final Charset charset;

    public Base64() {
        this(StandardCharsets.UTF_8);
    }

    public Base64(Charset charset) {
        this.charset = charset;
    }

    @Override
    public String apply(Object obj) {
        if (obj instanceof String) {
            return java.util.Base64.getEncoder()
                    .encodeToString(((String) obj).getBytes(charset));
        }

        throw new InvalidIntrinsicValue(String.valueOf(obj));
    }

    @Override
    public String name() {
        return INTRINSIC_NAME;
    }
}
