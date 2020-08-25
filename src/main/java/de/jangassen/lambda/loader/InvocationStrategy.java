package de.jangassen.lambda.loader;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface InvocationStrategy {
    Object invoke(Callable<Object> method);
}
