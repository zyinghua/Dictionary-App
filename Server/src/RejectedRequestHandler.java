import Messages.FailureResponse;
import Utils.Operation;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RejectedRequestHandler extends Thread{
    private final Socket clientConn;
    public RejectedRequestHandler(Socket clientConn)
    {
        this.clientConn = clientConn;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(clientConn.getOutputStream());
            oos.writeObject(new FailureResponse(Operation.UNKNOWN, "Server is overloaded. Please try again later."));
            oos.flush();

            oos.close();
            clientConn.close();
        } catch (EOFException e)
        {
            System.out.println("Connection illegally ended by client " + clientConn.getInetAddress());
        }
        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
