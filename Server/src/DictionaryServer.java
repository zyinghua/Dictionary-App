import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class DictionaryServer {
    private static final String defaultFileName = "dictionary_data.json"; // Used when no default file is provided

    public static void main(String[] args){
        if (args.length < 1 || args.length > 2) {
            // Handle invalid number of arguments
            System.out.println("Usage: java -jar DictionaryServer.jar <port> <dictionary-file>[optional]");
            System.exit(1);
        }

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]))) {
            System.out.println("Server started.");

            String fileName = args.length == 2 ? args[1] : defaultFileName;

            Dictionary dict = new Dictionary(args.length == 2, fileName);
            AutoFileSaver autoFileSaver = new AutoFileSaver(fileName, dict);
            autoFileSaver.start();

            RequestReceiver requestReceiver = new RequestReceiver(serverSocket, dict);
            requestReceiver.start();

            String command = "";
            Scanner scanner = new Scanner(System.in);

            while (!command.equals("quit")) {
                command = scanner.nextLine().toLowerCase();
            }

            requestReceiver.terminate();
            autoFileSaver.terminate();

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