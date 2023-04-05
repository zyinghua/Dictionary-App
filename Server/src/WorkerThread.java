/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

import Messages.*;
import Utils.Operation;
import Utils.UtilsItems;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkerThread extends Thread{
    private final int tid;
    private volatile BlockingQueue<Socket> clientQueue;
    private final Dictionary dict;
    private volatile boolean isRunning;
    private int keepAliveTimeSec = -1;
    private volatile ConcurrentHashMap<Integer, WorkerThread> additionalWorkerThreadList = null;
    private volatile AtomicInteger numRequestsProcessed;
    private volatile AtomicInteger verbose;

    public WorkerThread(int tid, BlockingQueue<Socket> clientQueue, Dictionary dict, AtomicInteger numRequestsProcessed, AtomicInteger verbose) {
        this.tid = tid;
        this.clientQueue = clientQueue;
        this.dict = dict;
        this.numRequestsProcessed = numRequestsProcessed;
        this.verbose = verbose;
        this.isRunning = true;
    }

    public WorkerThread(int tid, BlockingQueue<Socket> clientQueue, Dictionary dict, AtomicInteger numRequestsProcessed,
                        int keepAliveTimeSec, ConcurrentHashMap<Integer, WorkerThread> additionalWorkerThreadList, AtomicInteger verbose) {
        this.tid = tid;
        this.clientQueue = clientQueue;
        this.dict = dict;
        this.numRequestsProcessed = numRequestsProcessed;
        this.keepAliveTimeSec = keepAliveTimeSec;
        this.additionalWorkerThreadList = additionalWorkerThreadList;
        this.verbose = verbose;
        this.isRunning = true;
    }

    private Response handleRequest(Request request) {
        Response response;

        try{
            switch (request.getOp()) {
                case ALIVE_MESSAGE -> {response = new SuccessResponse(Operation.ALIVE_MESSAGE);}
                case ADD_WORD -> {response = this.dict.addAWord(request.getWord(), ((AddUpdateRequest) request).getMeanings());}
                case REMOVE_WORD -> {response = this.dict.removeAWord(request.getWord());}
                case QUERY_WORD -> {response = this.dict.queryAWord(request.getWord());}
                case UPDATE_WORD -> {response = this.dict.updateAWord(request.getWord(), ((AddUpdateRequest) request).getMeanings());}
                default -> {response = new FailureResponse(Operation.UNKNOWN, "Unknown operation.");}
            }
        } catch (NullPointerException e) {
            response = new FailureResponse(Operation.UNKNOWN, "[Internal Error] Null Pointer Exception encountered on processing request: " + e.getMessage());
        } catch (Exception e) {
            response = new FailureResponse(Operation.UNKNOWN, "[Internal Error] Exception encountered on processing request: " + e.getMessage());
        }

        return response;
    }
    @Override
    public void run() {
        while(isRunning)
        {
            Socket clientConn = null;

            try {
                if(this.keepAliveTimeSec != -1)
                    clientConn = clientQueue.poll(this.keepAliveTimeSec, java.util.concurrent.TimeUnit.SECONDS);
                else
                    clientConn = clientQueue.poll();

                if(clientConn != null)
                {
                    ObjectOutputStream oos = new ObjectOutputStream(clientConn.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(clientConn.getInputStream());

                    Request request = (Request) ois.readObject();

                    oos.writeObject(this.handleRequest(request));
                    oos.flush();

                    oos.close();
                    ois.close();
                    clientConn.close();
                    numRequestsProcessed.incrementAndGet();

                    if (verbose.get() >= UtilsItems.VERBOSE_ON_LOW)
                        System.out.println("[Worker thread " + this.tid + "] processed a request from "
                                + clientConn.getInetAddress() + ".\n" + request.toString() + "\nTotal Requests Processed Since Server Start-Up: " + numRequestsProcessed.get());
                }
                else if(this.getTid() >= 20)
                {
                    this.isRunning = false;

                    if(this.additionalWorkerThreadList != null)
                        this.additionalWorkerThreadList.remove(this.getTid());

                    if(this.verbose.get() >= UtilsItems.VERBOSE_ON_LOW)
                        System.out.println("[Additional Worker thread " + this.tid + "] terminated, due to being idle for " + this.keepAliveTimeSec + " seconds.");
                }

            } catch (InterruptedException e) {
                if (clientConn != null) {
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(clientConn.getOutputStream());

                        oos.writeObject(new FailureResponse(Operation.UNKNOWN, "Process interrupted. Possible cause: server is shutting down."));
                        oos.flush();

                        oos.close();
                        clientConn.close();
                    } catch (IOException ioe) {
                        System.err.println("[Worker thread " + this.tid + "] Error on closing client connection: " + ioe.getMessage());
                    }
                }

                if(!isRunning)
                {
                    //Termination called. Close any connections in the blocking queue
                    while (!clientQueue.isEmpty()) {
                        Socket clientConnToClose = clientQueue.poll();
                        try {
                            if (clientConnToClose != null)
                            {
                                ObjectOutputStream oos = new ObjectOutputStream(clientConnToClose.getOutputStream());

                                oos.writeObject(new FailureResponse(Operation.UNKNOWN, "Server is shutting down."));
                                oos.flush();

                                oos.close();
                                clientConnToClose.close();
                            }
                        } catch (IOException ioe) {
                            System.err.println("[Worker thread " + this.tid + "] Error closing socket: " + ioe.getMessage());
                        } catch (NullPointerException npe) {
                            System.err.println("[Worker thread " + this.tid + "] Null Pointer Exception encountered when closing unprocessed sockets: " + npe.getMessage());
                        }
                    }
                }

                System.exit(0);

            } catch (EOFException e) {
                System.err.println("Connection unexpectedly ended by client "
                        + (clientConn.getInetAddress() == null? clientConn.getInetAddress() : "UNKNOWN") + "\n" + e.getMessage());
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("[Worker thread " + this.tid + "] Exception on processing socket connection: " +  e.getMessage());
            } catch (NullPointerException e) {
                System.err.println("[Worker thread " + this.tid + "] Null Pointer Exception: " + e.getMessage() + ", Cause: " + e.getCause());
            } catch (Exception e) {
                System.err.println("[Worker thread " + this.tid + "] Exception: " + e.getMessage());
            }
        }

        System.out.println("[Worker thread " + this.tid + "] Terminated.");
    }

    public int getTid() {
        return tid;
    }

    public synchronized void terminate() {
        this.isRunning = false;
    }
}
