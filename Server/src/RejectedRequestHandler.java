/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

import Messages.UnprocessedResponse;
import Utils.*;

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
            oos.writeObject(new UnprocessedResponse(UtilsItems.SERVER_OVERLOAD_REJECT_MSG));
            oos.flush();

            oos.close();
            clientConn.close();
        } catch (EOFException e)
        {
            System.err.println("[Rejected Request Handler] Connection illegally ended by client " + clientConn.getInetAddress() + "\n" + e.getMessage());
        } catch (IOException e) {
            System.err.println("[Rejected Request Handler] IO Exception: " + e.getMessage() + ". On client address: " + clientConn.getInetAddress());
        } catch (NullPointerException e) {
            System.err.println("[Rejected Request Handler] Null Pointer Exception: " + e.getMessage() + ". On client address: " + clientConn.getInetAddress());
        } catch (Exception e) {
            System.err.println("[Rejected Request Handler] Exception: " + e.getMessage() + ". On client address: " + clientConn.getInetAddress());
        }
    }
}
