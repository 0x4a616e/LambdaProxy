package de.jangassen.lambda.watcher;

import java.nio.file.Path;

@FunctionalInterface
public interface DeploymentChangeHandler {
    void handle(Path path);
}
