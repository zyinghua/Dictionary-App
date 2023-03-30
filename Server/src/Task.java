import Messages.AddUpdateRequest;
import Messages.*;

import java.io.*;
import java.net.Socket;

public class Task implements Runnable {
    Socket clientConn;
    Dictionary dict;

    public Task() {
    }

    public Task(Socket clientConn, Dictionary dict) {
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
            ObjectOutputStream oos = new ObjectOutputStream(clientConn.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(clientConn.getInputStream());

            Request request = (Request) ois.readObject();

            oos.writeObject(this.handleRequest(request));

            oos.close();
            ois.close();
            clientConn.close();
        } catch (EOFException e)
        {
            System.out.println("Connection illegally ended by client " + clientConn.getInetAddress());
        }
        catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: " + e + " | " + e.getMessage());
        }
    }
}