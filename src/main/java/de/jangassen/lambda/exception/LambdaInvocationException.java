package de.jangassen.lambda.exception;

public class LambdaInvocationException extends RuntimeException {
    public LambdaInvocationException(Exception e) {
        super(e);
    }
}
