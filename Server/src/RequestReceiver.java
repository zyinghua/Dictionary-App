/*
    @Author: Yinghua Zhou
    Student ID: 1308266

    This thread is mainly responsible for receiving client requests and put them into the queue.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

public class RequestReceiver extends Thread{
    private volatile boolean isRunning;
    private volatile ServerSocket serverSocket;
    private final WorkerPoolManager workerPoolManager;

    public RequestReceiver(ServerSocket serverSocket, WorkerPoolManager workerPoolManager)
    {
        this.serverSocket = serverSocket;
        this.workerPoolManager = workerPoolManager;
        this.isRunning = true;
    }

    public void run(){
        System.out.println("[Request Receiver] Running...");

        while(this.isRunning)
        {
            try {
                Socket clientConn = serverSocket.accept(); // wait and accept a connection
                System.out.println("Received a client request from: " + clientConn.getInetAddress());

                workerPoolManager.addClient(clientConn);
            } catch (SocketException e) {
                if (e.getMessage().equals("Socket closed")) {
                    // server socket is closed, this thread was called to terminate
                    System.out.println("[Request Receiver] Server closed.");
                } else {
                    System.out.println("[Request Receiver] Socket Exception: " + e.getMessage());
                }
            }
            catch (IOException e) {
                System.out.println("[Request Receiver] IO Exception: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[Request Receiver] Exception: " + e.getMessage());
            }
        }

        System.out.println("[Request Receiver] Finished.");
    }

    public synchronized void terminate()
    {
        this.isRunning = false;
    }
}
