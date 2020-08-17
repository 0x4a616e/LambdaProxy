package de.jangassen.lambda.watcher;

import de.jangassen.lambda.exception.ChangeWatcherException;

import java.io.IOException;
import java.nio.file.*;

public class DeploymentChangeWatcher implements Runnable {

    private final DeploymentChangeHandler deploymentChangeHandler;
    private final Path watchPath;

    public DeploymentChangeWatcher(Path watchPath, DeploymentChangeHandler deploymentChangeHandler) {
        this.watchPath = watchPath;
        this.deploymentChangeHandler = deploymentChangeHandler;
    }

    @Override
    public void run() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            watchPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey wk;
            do {
                wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    final Path changed = (Path) event.context();

                    System.out.println("Change in " + changed + " detected.");
                    deploymentChangeHandler.handle();
                    System.out.println("API reloaded.");
                }
            } while (wk.reset());
        } catch (InterruptedException | IOException e) {
            throw new ChangeWatcherException(e);
        }
    }
}
