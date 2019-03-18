package httpserver.anton.tasks;

/**
 * @author Dmitry Bryzhatov
 * @since 2019-02-01
 */
public interface Task extends Runnable {
    void doTask();

    @Override
    default void run() {
        doTask();
    }
}
