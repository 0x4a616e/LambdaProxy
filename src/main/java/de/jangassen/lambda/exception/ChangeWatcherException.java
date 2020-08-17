package de.jangassen.lambda.exception;

public class ChangeWatcherException extends RuntimeException {
    public ChangeWatcherException(Exception e) {
        super(e);
    }
}
