package de.jangassen.lambda.watcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeploymentChangeWatcherTest {

    @Mock
    WatchService watchService;

    @Mock
    WatchKey watchKey;

    @Mock
    WatchEvent<Path> watchEvent;

    private Path changedPath;

    @BeforeEach
    void init() {
        changedPath = null;
    }

    @Test
    void testChangeWatcher() throws InterruptedException {
        Path dir = new File("change").toPath();

        when(watchService.take()).thenReturn(watchKey);
        when(watchKey.pollEvents()).thenReturn(Collections.singletonList(watchEvent));
        when(watchEvent.context()).thenReturn(dir);

        DeploymentChangeWatcher watcher = new DeploymentChangeWatcher(new File("dir").toPath(), this::handle);
        watcher.watch(watchService);

        assertEquals(dir, changedPath);
    }

    private void handle(Path path) {
        changedPath = path;
    }
}