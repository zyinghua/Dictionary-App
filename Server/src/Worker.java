import Messages.AddUpdateRequest;
import Utils.*;
import Messages.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Worker extends Thread {
    /*Thread class to handle connection from the server*/

    Socket clientConn;
    Dictionary dict;

    public Worker() {
    }

    public Worker(Socket clientConn, Dictionary dict) {
        this.clientConn = clientConn;
        this.dict = dict;
    }

    private Response handleRequest(Request request) {
        Response response = null;

        switch (request.getOp()) {
            case ADD_WORD -> {response = this.dict.addAWord(request.getWord(), ((AddUpdateRequest) request).getMeanings());}
            case REMOVE_WORD -> {response = this.dict.removeAWord(request.getWord());}
            case QUERY_WORD -> {response = this.dict.queryAWord(request.getWord());}
            case UPDATE_WORD -> {response = this.dict.updateAWord(request.getWord(), ((AddUpdateRequest) request).getMeanings());}
        }

        return response;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = clientConn.getInputStream();
            OutputStream outputStream = clientConn.getOutputStream();

            ObjectOutputStream oos = new ObjectOutputStream(clientConn.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(clientConn.getInputStream());

            while(true)
            {
                Request request = (Request) ois.readObject();

                if (!request.equals(Utils.terminateSignal))
                {
                    oos.writeObject(this.handleRequest(request));
                }
                else {
                    System.out.println("Connection from client " + clientConn.getInetAddress() + " ended.");
                    oos.close();
                    ois.close();
                    clientConn.close();
                    break;
                }
            }

        } catch (EOFException e)
        {
            System.out.println("Connection ended by client " + clientConn.getInetAddress());
        }
        catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: " + e + " | " + e.getMessage());
        }
    }
}