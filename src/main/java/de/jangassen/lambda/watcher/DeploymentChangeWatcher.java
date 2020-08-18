package de.jangassen.lambda.watcher;

import de.jangassen.lambda.LambdaProxy;
import de.jangassen.lambda.exception.ChangeWatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;

public class DeploymentChangeWatcher implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(LambdaProxy.class);

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

            watch(watchService);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ChangeWatcherException(e);
        } catch (IOException e) {
            throw new ChangeWatcherException(e);
        }
    }

    void watch(WatchService watchService) throws InterruptedException {
        WatchKey wk;
        do {
            wk = watchService.take();
            for (WatchEvent<?> event : wk.pollEvents()) {
                final Path changed = (Path) event.context();

                logger.info("Change in '{}' detected.", changed);
                deploymentChangeHandler.handle(changed);
                logger.info("API reloaded.");
            }
        } while (wk.reset());
    }
}
