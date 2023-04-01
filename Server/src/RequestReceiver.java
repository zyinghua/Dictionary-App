/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class RequestReceiver extends Thread{
    private volatile boolean isRunning = true;
    private ServerSocket serverSocket;
    private WorkerPoolManager workerPoolManager;
    private Dictionary dict;

    public RequestReceiver(ServerSocket serverSocket, Dictionary dict,
                           int corePoolSize, int maxPoolSize, long keepAliveTime, int queueCapacity)
    {
        this.serverSocket = serverSocket;
        this.dict = dict;
        this.workerPoolManager = new WorkerPoolManager(corePoolSize, maxPoolSize, keepAliveTime, queueCapacity);
    }

    public void run(){
        System.out.println("[Request Receiver] Running...");

        while(this.isRunning)
        {
            try {
                Socket clientConn = serverSocket.accept(); // wait and accept a connection
                System.out.println("Received a client request from: " + clientConn.getInetAddress());

                this.workerPoolManager.executeTask(new Task(clientConn, dict));
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

    public void terminate()
    {
        this.isRunning = false;
        this.workerPoolManager.terminate();
    }
}
