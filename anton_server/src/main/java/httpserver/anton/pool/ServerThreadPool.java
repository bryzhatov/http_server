package httpserver.anton.pool;

import httpserver.anton.pool.util.ThreadPoolDTO;
import httpserver.anton.tasks.Task;
import lombok.Data;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-01
 */
@Log4j
@Data
public class ServerThreadPool extends ThreadPool {
    private final ReentrantLock threadsPoolLock = new ReentrantLock();
    private final List<TaskThread> threadsPool = new ArrayList<>();
    private final ReentrantLock taskPoolLock = new ReentrantLock();
    private final List<Task> taskPool = new ArrayList<>();
    private int countThreads;

    public ServerThreadPool(ThreadPoolDTO threadPoolDTO) {
        super(threadPoolDTO);
        initCoreThreads(coreCountThreads);
    }

    private void initCoreThreads(int coreCountThread) {
        for (int i = 0; i < coreCountThread; i++) {
            TaskThread taskThread = new TaskThread();
            startThread(taskThread);
            threadsPool.add(taskThread);
        }
        countThreads = threadsPool.size();
    }

    @Override
    public boolean handle(Task task) {
        TaskThread taskThread = null;
        try {
            threadsPoolLock.lock();
            if (threadsPool.size() > 0) {
                taskThread = threadsPool.remove(0);
            } else {
                if (countThreads < maxCountThreads) {
                    taskThread = new TaskThread(task);
                    taskThread.start();
                    countThreads++;
                    return true;
                }
            }

        } finally {
            threadsPoolLock.unlock();
        }

        if (taskThread != null) {
            taskThread.setTask(task);
            return true;
        } else {
            try {
                taskPoolLock.lock();
                if (taskPool.size() < 100) {
                    taskPool.add(task);
                    return true;
                }
            } finally {
                taskPoolLock.unlock();
            }
        }
        return false;
    }

    public List<Runnable> shutdownNow() {
        for (TaskThread taskThread : threadsPool) {
            taskThread.interrupt();
        }

        List<Runnable> noRunnable = new ArrayList<>();
        try {
            taskPoolLock.lock();
            noRunnable.addAll(taskPool);
        } finally {
            taskPoolLock.unlock();
        }
        return noRunnable;
    }

    public void startThread(TaskThread taskThread) {
        taskThread.start();
        while (true) {
            if (taskThread.getState().equals(Thread.State.TIMED_WAITING)) {
                break;
            }
        }
    }

    public class TaskThread extends Thread {
        private final ReentrantLock WAIT = new ReentrantLock();
        private final Condition WAIT_MONITOR = WAIT.newCondition();
        private volatile Task task;

        TaskThread() {
        }

        TaskThread(Task task) {
            this.task = task;
        }

        void setTask(Task task) {
            try {
                WAIT.tryLock(150, TimeUnit.MILLISECONDS);
                this.task = task;
                WAIT_MONITOR.signal();
            } catch (InterruptedException e) {
                log.error("Can't lock monitor.", e);
            } finally {
                WAIT.unlock();
            }
        }

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    try {
                        WAIT.lock();
                        if (task == null) {
                            WAIT_MONITOR.await(timeIdle, TimeUnit.MILLISECONDS);

                            try {
                                threadsPoolLock.lock();
                                threadsPool.remove(this);
                            } finally {
                                threadsPoolLock.unlock();
                            }

                            if (task == null) {
                                try {
                                    taskPoolLock.lock();
                                    if (taskPool.size() > 0) {
                                        task = taskPool.remove(0);
                                    }
                                } finally {
                                    taskPoolLock.unlock();
                                }

                                if (task == null) {
                                    try {
                                        threadsPoolLock.lock();
                                        if (countThreads > coreCountThreads) {
                                            countThreads--;
                                            interrupt();
                                            break;
                                        }
                                    } finally {
                                        threadsPoolLock.unlock();
                                    }
                                }
                            }
                        }
                    } finally {
                        WAIT.unlock();
                    }

                    if (task != null) {
                        task.doTask();
                        task = null;
                    }

                    try {
                        threadsPoolLock.lock();
                        threadsPool.add(this);
                    } finally {
                        threadsPoolLock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                log.info("Interrupt thread: " + Thread.currentThread().getName());
            }
        }
    }
}
