package Server;

public class WorkerPoolManager {
    private final int poolSize;
    private final Worker[] workerPools;

    public WorkerPoolManager(int poolSize)
    {
        this.poolSize = poolSize;
        this.workerPools = new Worker[poolSize];

        for(int i = 0; i < poolSize; i++)
            this.workerPools[i] = new Worker();
    }

    public Worker getWorkerPool(int index)
    {
        return this.workerPools[index];
    }

    public int getPoolSize()
    {
        return this.poolSize;
    }
}
