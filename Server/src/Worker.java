import Messages.AddUpdateRequest;
import Utils.*;

import Messages.Request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

    private String handleRequest(String encodedRequest) {
        Request request = Utils.decodeRequest(encodedRequest);
        String result = "failure";

        switch (request.getOp()) {
            case ADD_WORD -> {result = this.dict.addAWord(request.getWord(), ((AddUpdateRequest) request).getMeanings());}
            case REMOVE_WORD -> {result = this.dict.removeAWord(request.getWord());}
            case QUERY_WORD -> {
                ArrayList<String> meanings = this.dict.queryAWord(request.getWord());
                if (meanings != null) result = meanings.toString();}
            case UPDATE_WORD -> {result = this.dict.updateAWord(request.getWord(), ((AddUpdateRequest) request).getMeanings());}
        }

        return result;
    }

    @Override
    public void run() {
        try {
            DataOutputStream dos = new DataOutputStream(clientConn.getOutputStream());
            DataInputStream dis = new DataInputStream(clientConn.getInputStream());

            while(true)
            {
                String request = dis.readUTF();

                if (!request.equals(Utils.terminateSignal))
                {
                    dos.writeUTF(this.handleRequest(request));
                }
                else {
                    System.out.println("Connection from client " + clientConn.getInetAddress() + " ended.");
                    dos.close();
                    dis.close();
                    clientConn.close();
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}