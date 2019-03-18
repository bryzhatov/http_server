package httpserver.anton.pool.factory;

import httpserver.anton.pool.ExecutorThreadPool;
import httpserver.anton.pool.ServerThreadPool;
import httpserver.anton.pool.ThreadPool;
import httpserver.anton.pool.util.ThreadPoolDTO;

public class ThreadPoolFactory {
    private final ThreadPoolDTO threadPoolDTO;

    public ThreadPoolFactory(ThreadPoolDTO threadPoolDTO) {
        this.threadPoolDTO = threadPoolDTO;
    }

    public ThreadPool getPool(String type) {
        switch (type) {
            case "executor":
                return new ExecutorThreadPool(threadPoolDTO);
            case "server":
                return new ServerThreadPool(threadPoolDTO);
            default:
                return new ExecutorThreadPool(threadPoolDTO);
        }
    }
}
