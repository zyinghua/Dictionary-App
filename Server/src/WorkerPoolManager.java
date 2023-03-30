import java.util.concurrent.*;

public class WorkerPoolManager {
    private static final int timeout = 5000;
    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final int queueCapacity;
    private ThreadPoolExecutor threadPoolExecutor;
    public WorkerPoolManager(int corePoolSize, int maxPoolSize, long keepAliveTime, int queueCapacity) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.queueCapacity = queueCapacity;

        this.initialise();
    }

    private void initialise()
    {
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueCapacity);
        this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                java.util.concurrent.TimeUnit.SECONDS, queue);
        System.out.println("[Worker Pool Manager] Initialised with corePoolSize: " + corePoolSize + ", maxPoolSize: " + maxPoolSize + ", keepAliveTime: " + keepAliveTime + ", queueCapacity: " + queueCapacity);
    }

    public void executeTask(Runnable task)
    {
        try {
            this.threadPoolExecutor.execute(task);
        } catch (RejectedExecutionException e) {
            System.err.println("[Worker Pool Manager] Rejected Execution (Task from client: "
                    + ((Task) task).getClientConn().getInetAddress() + ").\n Exception: " + e.getMessage());

            // send a failure response to the client
            new RejectedRequestHandler(((Task) task).getClientConn()).start();

        } catch (NullPointerException e) {
            System.err.println("[Worker Pool Manager] Null Pointer Exception: " + e.getMessage() + ", Cause: " + e.getCause());
        }
        catch (Exception e) {
            System.err.println("[Worker Pool Manager] Exception: " + e.getMessage());
        }
    }

    public void terminate()
    {
        this.threadPoolExecutor.shutdown();
        try {
            if (!this.threadPoolExecutor.awaitTermination(timeout, TimeUnit.SECONDS)) { // block until all tasks are completed or timeout is reached
                this.threadPoolExecutor.shutdownNow(); // forcefully shut down if timeout is reached
            }
        } catch (InterruptedException ex) {
            this.threadPoolExecutor.shutdownNow(); // forcefully shut down if interrupted
        }
    }
}
