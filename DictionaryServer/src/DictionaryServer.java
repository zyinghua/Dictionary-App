/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

import Utils.UtilsItems;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class DictionaryServer {
    public static final String USAGE = "Usage: java -jar DictionaryServer.jar <port> <dictionary-file>[optional]";
    private static final String defaultFileName = "dictionary_data.json"; // Used when no default file is provided
    private static final int autoFileSaveIntervalMs = 5000; // 5 seconds
    private static final int corePoolSize = 20;
    private static final int maximumPoolSize = 40;
    private static final int keepAliveTimeSec = 30;
    private static final int queueCapacity = 100;

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            // Handle invalid number of arguments
            System.out.println(USAGE);
            System.exit(1);
        }

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]))) {
            System.out.println("Server starting...\n");

            String fileName = args.length == 2 ? args[1] : defaultFileName;

            Dictionary dict = new Dictionary(args.length == 2, fileName);
            AtomicInteger verbose = new AtomicInteger(UtilsItems.VERBOSE_OFF);
            BlockingQueue<Socket> clientQueue = new ArrayBlockingQueue<>(queueCapacity);

            WorkerPoolManager workerPoolManager = new WorkerPoolManager(corePoolSize, maximumPoolSize, keepAliveTimeSec, clientQueue, dict, verbose);

            AutoFileSaver autoFileSaver = new AutoFileSaver(autoFileSaveIntervalMs, dict, verbose);
            autoFileSaver.start();

            RequestReceiver requestReceiver = new RequestReceiver(serverSocket, workerPoolManager, verbose);
            requestReceiver.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                requestReceiver.terminate();
                autoFileSaver.terminate();
                workerPoolManager.terminate();
            }));

            System.out.println("\nServer started successfully. Listening on port " + args[0] + " for incoming connections...");

            String command = "";
            Scanner scanner = new Scanner(System.in);

            while (!command.equals("quit")) {
                System.out.println("[Admin panel] Type 'help' to see the valid commands: ");
                command = scanner.nextLine().toLowerCase();

                switch (command) {
                    case "quit" -> {
                        System.out.println("\n--------------------------");
                        System.out.println("Server is shutting down...");
                        System.out.println("--------------------------\n");
                    }
                    case "save" -> {
                        workerPoolManager.getDict().writeDictDataToFile();
                        System.out.println("\n-------------------------");
                        System.out.println("Dictionary saved to file.");
                        System.out.println("-------------------------\n");
                    }
                    case "check dictionary" -> {
                        System.out.println("\n------------------------------");
                        System.out.println(dict);
                        System.out.println("------------------------------\n");
                    }
                    case "check requests" -> {
                        System.out.println("\n---------------------------------");
                        System.out.println("Number of requests processed: " + workerPoolManager.getNumRequestsProcessed().get());
                        System.out.println("---------------------------------\n");
                    }
                    case "check threads" -> {
                        System.out.println("\n---------------------------------");
                        System.out.println("Number of worker threads: " + (corePoolSize + workerPoolManager.getAdditionalWorkerThreadList().size()));
                        System.out.println("---------------------------------\n");
                    }
                    case "verbose 0" -> {
                        verbose.set(0);
                        System.out.println("\n-------------------------");
                        System.out.println("Verbose mode is now: OFF");
                        System.out.println("-------------------------\n");
                    }
                    case "verbose 1" -> {
                        verbose.set(1);
                        System.out.println("\n-------------------------");
                        System.out.println("Verbose mode is now: LOW");
                        System.out.println("-------------------------\n");
                    }
                    case "verbose 2" -> {
                        verbose.set(2);
                        System.out.println("\n-------------------------");
                        System.out.println("Verbose mode is now: HIGH");
                        System.out.println("-------------------------\n");
                    }
                    case "help" -> System.out.printf("""
                                                        
                            ------------------VALID COMMANDS--------------------
                            Type 'quit' to stop the server.
                            Type 'save' to save the dictionary data into file.
                            Type 'check dictionary' to check the dictionary data.
                            Type 'check requests' to check the number of requests processed.
                            Type 'check threads' to check the number of current worker threads.
                            Type 'verbose {0,1,2}' to toggle verbose mode. (0 = off, 1 = Low, 2 = High) Verbose mode is currently: %s
                            ----------------------------------------------------
                                                        
                            """, verbose.get());
                    default -> {
                        System.out.println("\n****************");
                        System.out.println("Invalid command.");
                        System.out.println("****************\n");
                    }
                }
            }

            autoFileSaver.terminate();
            workerPoolManager.terminate();
            requestReceiver.terminate();

        } catch (NumberFormatException e) {
            System.out.println(USAGE);
            System.err.println("[Server failed to start] Port must be an integer.\n" + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("[Server] IO Exception encountered on the server.\n" + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("[Server failed to start] Port must be between 0 and 65535.\n" + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("[Server] Exception encountered on the server: " + e.getMessage());
            System.exit(1);
        }
    }
}