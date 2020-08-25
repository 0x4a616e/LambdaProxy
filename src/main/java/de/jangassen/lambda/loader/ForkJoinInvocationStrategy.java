package de.jangassen.lambda.loader;

import de.jangassen.lambda.exception.LambdaInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class ForkJoinInvocationStrategy implements InvocationStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinInvocationStrategy.class);

    private final ClassLoader classLoader;

    public ForkJoinInvocationStrategy(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Object invoke(Callable<Object> callable) {
        ForkJoinPool forkJoinPool = createForkJoinPool(classLoader);
        ForkJoinTask<Object> submit = forkJoinPool.submit(callable);

        try {
            return submit.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LambdaInvocationException(e);
        } catch (ExecutionException e) {
            throw new LambdaInvocationException(e);
        } finally {
            forkJoinPool.shutdown();
        }
    }

    private ForkJoinPool createForkJoinPool(ClassLoader classLoader) {
        return new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
                pool -> new LambdaForkJoinWorkerThread(pool, classLoader),
                (t, e) -> LOGGER.error("Uncaught exception while executing task", e),
                false);
    }

    static class LambdaForkJoinWorkerThread extends ForkJoinWorkerThread {

        protected LambdaForkJoinWorkerThread(ForkJoinPool pool, ClassLoader classLoader) {
            super(pool);
            setContextClassLoader(classLoader);
        }
    }
}
