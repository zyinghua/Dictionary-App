import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class RequestReceiver extends Thread{
    private volatile boolean isRunning = true;
    private ServerSocket serverSocket;
    private Dictionary dict;

    public RequestReceiver(ServerSocket serverSocket, Dictionary dict)
    {
        this.serverSocket = serverSocket;
        this.dict = dict;
    }

    public void run(){
        System.out.println("[Request Receiver] Running...");

        while(this.isRunning)
        {
            try {
                Socket clientConn = serverSocket.accept(); // wait and accept a connection
                System.out.println("Accepted a client: " + clientConn.getInetAddress());

                new Worker(clientConn, dict).start(); // Establish a new thread to handle the connection

            } catch (IOException e) {
                System.out.println("[Request Receiver] Exception: " + e.getMessage());
            }
        }

        System.out.println("[Request Receiver] Finished");
    }

    public void terminate()
    {
        this.isRunning = false;
    }
}
