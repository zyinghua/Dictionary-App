package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DictionaryServer {
    private static final String defaultFileName = "dictionary_data.json"; // Used when no default file is provided

    public static void main(String[] args){
        if (args.length < 1 || args.length > 2) {
            // Handle invalid number of arguments
            System.out.println("Usage: java -jar DictionaryServer.jar <port> <dictionary-file>");
            System.exit(1);
        }

        try (ServerSocket server = new ServerSocket(Integer.parseInt(args[0]))) {
            System.out.println("Server started.");
            Dictionary dict = new Dictionary(args[1]);

            while(true)
            {
                Socket clientConn = server.accept(); // wait and accept a connection
                System.out.println("Accepted a client: " + clientConn.getInetAddress());

                new Worker(clientConn, dict).start(); // Establish a new thread to handle the connection
            }
        } catch (NumberFormatException e) {
            System.out.println("Port must be an integer.");
            System.exit(1);
        }
        catch (IOException e) {
            System.out.println("IO Exception encountered on starting up the server.");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.out.println("Port must be between 0 and 65535");
            System.exit(1);
        }
    }
}