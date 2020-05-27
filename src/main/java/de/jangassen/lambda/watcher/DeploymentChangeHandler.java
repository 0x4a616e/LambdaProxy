package de.jangassen.lambda.watcher;

@FunctionalInterface
public interface DeploymentChangeHandler {
    void handle();
}
