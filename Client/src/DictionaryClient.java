/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

import Messages.*;
import Utils.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DictionaryClient {
    private static final int REQUEST_TIMEOUT = 7000;
    private static final String USAGE = "Usage: java -jar Client.jar <serverAddress> <serverPort> <Open with GUI? '0' for No, '1' for Yes>";
    public static final String ERROR_EMPTY_WORD = "Please enter a word.";
    public static final String ERROR_INVALID_WORD = "Word must contain at least one English letter and not have any spaces, please try again.";
    public static final String ERROR_EMPTY_MEANING = "Please enter at least one meaning.";

    private static String promptOperation(Scanner sc)
    {
        while(true)
        {
            System.out.println("Input 'add' for adding a new word.");
            System.out.println("Input 'remove' for removing a word.");
            System.out.println("Input 'query' for querying a word.");
            System.out.println("Input 'update' for updating a word.");
            System.out.println("Input 'quit' to quit the program.\n");

            System.out.println("Please select an operation from above: ");
            String op = sc.nextLine().toLowerCase();

            if (List.of("add", "remove", "query", "update", "quit").contains(op))
            {
                return op;
            }
            else
            {
                System.out.println("The input is not acceptable, please re-enter.\n");
            }
        }
    }

    public static boolean checkWordValidity(String word)
    {
        return !word.isEmpty() && word.matches(UtilsItems.WORD_REGEX);
    }

    private static String promptWord(Scanner sc)
    {
        while(true)
        {
            System.out.println("Please enter the word: ");
            String word = sc.nextLine();

            if (checkWordValidity(word))
            {
                return word;
            }
            else if(word.isEmpty())
            {
                System.out.println(ERROR_EMPTY_WORD + "\n");
            }
            else
            {
                System.out.println(ERROR_INVALID_WORD + "\n");
            }
        }
    }

    public static Response checkServerValidity(boolean isGUI, String serverAddress, int serverPort)
    {
        // Called on the start-up of the client, to check if the server availability and argument validity
        // Tries to send an ALIVE_MESSAGE to the server, if the server is not alive, it will return a FailureResponse
        Request request = new Request(Operation.ALIVE_MESSAGE, null);
        return sendRequest(isGUI, request, serverAddress, serverPort);
    }

    public static Response sendRequest(boolean isGUI, Request request, String serverAddress, int serverPort)
    {
        // Responsible for sending the request to the server and returning the response, all exceptions caught here
        try
        {
            Socket client = new Socket(serverAddress, serverPort);
            client.setSoTimeout(REQUEST_TIMEOUT);

            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());

            oos.writeObject(request);
            oos.flush();

            Response response = (Response) ois.readObject();

            oos.close();
            ois.close();
            client.close();

            return response;

        }catch (SocketTimeoutException e) {
            String err = "Request timed out. This may because the server is not up at the moment or is not able to process requests.\n" + e.getMessage();

            if (isGUI)
            {
                return new FailureResponse(request.getOp(), err);
            }
            else {
                System.err.println(err);
                System.exit(1);
            }
        } catch (ClassNotFoundException e) {
            String err = "[Error on parsing response] Class not found: " + e.getMessage();
            System.err.println(err);
            return new FailureResponse(request.getOp(), err);
        }
        catch (NumberFormatException e) {
            // Exception happening on Command Line start-up
            System.out.println(USAGE);
            System.err.println(e.getMessage() + "\nPort must be an integer.\n" + e.getMessage());
            System.exit(1);
        } catch (UnknownHostException e) {
            // Exception happening on Command Line start-up
            System.out.println(USAGE);
            System.err.println("Cannot find the specified host. Please check host name: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            String err = "IO Exception encountered when connecting with the server: " + e.getMessage()
                    + ".\nThis may because the server is not up at the moment, or is overloaded.";

            if (isGUI)
            {
                return new FailureResponse(request.getOp(), err);
            }
            else {
                System.err.println(err);
                System.exit(1);
            }
        } catch (IllegalArgumentException e) {
            // Exception happening on Command Line start-up
            System.err.println(e.getMessage() + "\nPort must be between 0 and 65535.");
            System.exit(1);
        }

        return new FailureResponse(request.getOp(), "Unknown error when sending request.");
    }

    private static void openWithCUI(String[] args)
    {
        checkServerValidity(false, args[0], Integer.parseInt(args[1]));

        Scanner sc = new Scanner(System.in);
        boolean done = false;

        while(!done)
        {
            Request request = null;
            String op = promptOperation(sc);

            switch (op) {
                case "add" -> {
                    String wordToAdd = promptWord(sc);
                    ArrayList<String> meanings = new ArrayList<>();
                    while (true) {
                        System.out.println("""
                                    Add a meaning for the word, or：
                                     Press enter to conclude,
                                     Type '#' to cancel the current operation:\s""");
                        String input = sc.nextLine();

                        if (!input.isEmpty()) {
                            if (input.equals("#")) {
                                // Cancel the current operation
                                request = new Request(Operation.CANCELLED, null);
                                break;
                            }
                            else{
                                // Add the meaning to the list
                                meanings.add(input);
                            }
                        } else {
                            if(meanings.isEmpty())
                                // The user did not enter any meaning
                                System.out.println(ERROR_EMPTY_MEANING + "\n");
                            else {
                                // The user has finished entering meanings
                                request = new AddUpdateRequest(Operation.ADD_WORD, wordToAdd, meanings);
                                break;
                            }
                        }
                    }
                }
                case "remove" -> {
                    String wordToRemove = promptWord(sc);
                    request = new Request(Operation.REMOVE_WORD, wordToRemove);
                }
                case "query" -> {
                    String wordToQuery = promptWord(sc);
                    request = new Request(Operation.QUERY_WORD, wordToQuery);
                }
                case "update" -> {
                    String wordToUpdate = promptWord(sc);
                    ArrayList<String> newMeanings = new ArrayList<>();
                    while (true) {
                        System.out.println("""
                                Add a meaning for the word, or：
                                 Press enter to conclude,
                                 Type '#' to cancel the current operation:\s""");
                        String input = sc.nextLine();

                        if (!input.isEmpty()) {
                            if (input.equals("#")) {
                                // Cancel the current operation
                                request = new Request(Operation.CANCELLED, null);
                                break;
                            } else {
                                // Add the meaning to the list
                                newMeanings.add(input);
                            }
                        } else {
                            if (newMeanings.isEmpty())
                                // The user did not enter any meaning
                                System.out.println(ERROR_EMPTY_MEANING + "\n");
                            else {
                                // The user has finished entering meanings
                                request = new AddUpdateRequest(Operation.UPDATE_WORD, wordToUpdate, newMeanings);
                                break;
                            }
                        }
                    }
                }
                case "quit" -> done = true;
            }

            if (!done && request != null && request.getOp() != Operation.CANCELLED) {

                Response response = sendRequest(false, request, args[0], Integer.parseInt(args[1]));

                if (response instanceof QueryResponse)
                {
                    QueryResponse queryResponse = (QueryResponse) response;
                    System.out.println("\n------------------------------");
                    System.out.println("The meanings of the word '" + queryResponse.getWord() + "' are: ");
                    for (int i = 0; i < queryResponse.getMeanings().size(); i++)
                    {
                        System.out.println(i+1 + ": " + queryResponse.getMeanings().get(i));
                    }
                    System.out.println("------------------------------");
                    System.out.println("\n");
                } else if(response instanceof UnprocessedResponse)
                {
                    System.err.println(((UnprocessedResponse) response).getMessage());
                }
                else
                {
                    System.out.println(response.toString());
                }

            } else if (done) {
                System.out.println("Quitting the program...");
            } else if(request == null) {
                System.err.println("Invalid request. Please try again...\n");
            } else {
                System.out.println("Operation cancelled.\n");
            }
        }
    }

    public static void main(String[] args){
        if (args.length != 3) {
            // Handle invalid number of arguments
            System.out.println(USAGE);
            System.exit(1);
        }

        if (args[2].equals("0")) {
            // Start the CUI
            openWithCUI(args);
        } else if (args[2].equals("1")) {
            // Start the GUI
            new ClientMainGUI(args[0], Integer.parseInt(args[1]));
            System.out.println("GUI started.");
        } else {
            // Handle invalid argument
            System.err.println("\nInvalid third argument: " + args[2] + ". The third argument must be either 0 or 1.");
            System.err.println(USAGE);
            System.exit(1);
        }
    }
}
