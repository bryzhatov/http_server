package httpserver.anton.pool;

import httpserver.anton.pool.util.ThreadPoolDTO;
import httpserver.anton.tasks.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-01
 */
public class ExecutorThreadPool extends ThreadPool {
    private final ExecutorService executorService;

    public ExecutorThreadPool(ThreadPoolDTO threadPoolDTO) {
        super(threadPoolDTO);

        this.executorService = new ThreadPoolExecutor(
                coreCountThreads,
                maxCountThreads,
                timeIdle,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    @Override
    public boolean handle(Task task) {
        if (task != null) {
            executorService.execute(task);
            return true;
        }
        return false;
    }

    @Override
    public List<Runnable> shutdownNow() {
        return executorService.shutdownNow();
    }
}
