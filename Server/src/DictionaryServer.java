/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DictionaryServer {
    public static final String USAGE = "Usage: java -jar Server.jar <port> <dictionary-file>[optional]";
    private static final String defaultFileName = "dictionary_data.json"; // Used when no default file is provided
    private static final int corePoolSize = 20;
    private static final int maximumPoolSize = 40;
    private static final long keepAliveTime = 60L;
    private static final int queueCapacity = 100;

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            // Handle invalid number of arguments
            System.out.println(USAGE);
            System.exit(1);
        }

        RequestReceiver2 requestReceiver = null;
        AutoFileSaver autoFileSaver = null;

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]))) {
            System.out.println("Server started. Listening on port " + args[0] + " for incoming connections...");

            String fileName = args.length == 2 ? args[1] : defaultFileName;

            Dictionary dict = new Dictionary(args.length == 2, fileName);
            autoFileSaver = new AutoFileSaver(dict);
            autoFileSaver.start();

            requestReceiver = new RequestReceiver2(serverSocket, dict,
                    corePoolSize, maximumPoolSize, keepAliveTime, queueCapacity);
            requestReceiver.start();

            String command = "";
            Scanner scanner = new Scanner(System.in);

            while (!command.equals("quit")) {
                System.out.println("[Admin panel] Type 'help' to see the valid commands: ");
                command = scanner.nextLine().toLowerCase();

                switch (command) {
                    case "quit" -> System.out.println("Server stopping...");
                    case "save" -> {
                        dict.writeDictDataToFile();
                        System.out.println("Dictionary saved to file.");
                    }
                    case "check dictionary" -> System.out.println(dict.getDict().toString());
                    case "help" -> System.out.println("""
                            Type 'quit' to stop the server.
                            Type 'save' to save the dictionary data into file.
                            Type 'check dictionary' to check the dictionary data.
                            """);
                    default -> System.out.println("Invalid command.");
                }
            }

            requestReceiver.terminate();
            autoFileSaver.terminate();




        } catch (NumberFormatException e) {
            System.out.println(USAGE);
            System.err.println("Port must be an integer.\n" + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IO Exception encountered on starting up the server.\n" + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Port must be between 0 and 65535.\n" + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Exception encountered on the server: " + e.getMessage());

            if (requestReceiver != null)
                requestReceiver.terminate();

            if(autoFileSaver != null)
                autoFileSaver.terminate();
            System.exit(1);
        }
    }
}