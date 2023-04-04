/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

import Messages.FailureResponse;
import Utils.Operation;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class WorkerPoolManager {
    private final BlockingQueue<Socket> clientQueue;
    private final WorkerThread[] workerThreads;
    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final int queueCapacity;
    private final Dictionary dict;
    public WorkerPoolManager(int corePoolSize, int maxPoolSize, long keepAliveTime, int queueCapacity, Dictionary dict) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.queueCapacity = queueCapacity;
        this.dict = dict;
        this.clientQueue = new ArrayBlockingQueue<>(queueCapacity);
        this.workerThreads = new WorkerThread[corePoolSize];
        this.initialise();
    }

    public void initialise()
    {
        for(int i = 0; i < corePoolSize; i++)
        {
            workerThreads[i] = new WorkerThread(i, this.clientQueue, this.dict);
            workerThreads[i].start();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Interrupt any running worker threads
            for (WorkerThread workerThread : workerThreads) {
                if (workerThread.isAlive()) {
                    workerThread.interrupt();
                }
            }

            // Close any connections in the blocking queue
            while (!clientQueue.isEmpty()) {
                Socket clientConn = clientQueue.poll();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(clientConn.getOutputStream());

                    oos.writeObject(new FailureResponse(Operation.UNKNOWN, "Server is shutting down."));
                    oos.flush();

                    oos.close();
                    clientConn.close();
                    clientConn.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }));
    }
}
