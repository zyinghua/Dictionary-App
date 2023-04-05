/*
    @Author: Yinghua Zhou
    Student ID: 1308266

    This thread is responsible for managing the worker threads,
    spawn new workers when necessary, and terminate workers when they are idle for a period.
 */

import java.net.Socket;
import java.util.concurrent.*;

public class WorkerPoolManager{
    private final int corePoolSize;
    private volatile ConcurrentHashMap<Integer, WorkerThread> additionalWorkerThreadList;
    private final int maximumPoolSize;
    private final int keepAliveTimeSec;
    private final Dictionary dict;
    private volatile BlockingQueue<Socket> clientQueue;
    private final WorkerThread[] workerThreads;

    public WorkerPoolManager(int corePoolSize, int maximumPoolSize, int keepAliveTimeSec, BlockingQueue<Socket> clientQueue, Dictionary dict) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTimeSec = keepAliveTimeSec;
        this.dict = dict;
        this.workerThreads = new WorkerThread[corePoolSize];
        this.clientQueue = clientQueue;
        this.initialise();
    }

    private void initialise()
    {
        for(int i = 0; i < this.corePoolSize; i++)
        {
            this.workerThreads[i] = new WorkerThread(i, this.clientQueue, this.dict);
            this.workerThreads[i].start();
        }

        this.additionalWorkerThreadList = new ConcurrentHashMap<>();
    }

    public void addClient(Socket clientConn)
    {
        boolean success = clientQueue.offer(clientConn);

        if (!success)
        {
            if(additionalWorkerThreadList.size() < maximumPoolSize) // Whether max pool size is reached
            {
                // Create a new worker thread
                int newWorkerId = corePoolSize + additionalWorkerThreadList.size(); // First additional worker thread will have tid = corePoolSize
                WorkerThread newWorkerThread = new WorkerThread(newWorkerId, this.clientQueue, this.dict, this.keepAliveTimeSec, this.additionalWorkerThreadList);
                additionalWorkerThreadList.put(newWorkerId, newWorkerThread);
                newWorkerThread.start();

                // Add the client to the queue
                boolean added = this.clientQueue.offer(clientConn);

                if (!added) {
                    // send a request rejection response to the client
                    new RejectedRequestHandler(clientConn).start();
                }

            } else {
                // send a request rejection response to the client
                new RejectedRequestHandler(clientConn).start();
            }
        }
    }

    public void terminate()
    {
        for (WorkerThread workerThread : this.workerThreads) {
            workerThread.terminate();
        }
    }
}
