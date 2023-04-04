/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

import Messages.*;
import Utils.Operation;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class WorkerThread extends Thread{
    private final int tid;
    private BlockingQueue<Socket> clientQueue;
    private Dictionary dict;
    private volatile boolean isRunning;

    public WorkerThread(int tid, BlockingQueue<Socket> clientQueue, Dictionary dict) {
        this.tid = tid;
        this.clientQueue = clientQueue;
        this.dict = dict;
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
                clientConn = clientQueue.take();
                ObjectOutputStream oos = new ObjectOutputStream(clientConn.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(clientConn.getInputStream());

                Request request = (Request) ois.readObject();

                oos.writeObject(this.handleRequest(request));
                oos.flush();

                oos.close();
                ois.close();
                clientConn.close();

            } catch (InterruptedException e) {
                System.err.println("[Worker thread " + this.tid + "] interrupted: " + e.getMessage());

                if (clientConn != null) {
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(clientConn.getOutputStream());

                        oos.writeObject(new FailureResponse(Operation.UNKNOWN, "Server is shutting down."));
                        oos.flush();

                        oos.close();
                        clientConn.close();
                    } catch (IOException ioException) {
                        System.err.println("[Worker thread " + this.tid + "] Error on closing client connection: " + ioException.getMessage());
                    }
                }

                System.exit(0);

            } catch (EOFException e)
            {
                System.err.println("Connection unexpectedly ended by client "
                        + (clientConn.getInetAddress() == null? clientConn.getInetAddress() : "UNKNOWN") + "\n" + e.getMessage());
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error on processing request: " +  e.getMessage());
            } catch (NullPointerException e) {
                System.err.println("[Worker thread " + this.tid + "] Null Pointer Exception: " + e.getMessage() + ", Cause: " + e.getCause());
            } catch (Exception e) {
                System.err.println("[Worker thread " + this.tid + "] Exception: " + e.getMessage());
            }
        }
    }

    public int getTid() {
        return tid;
    }

    public synchronized void terminate() {
        this.isRunning = false;
    }
}
