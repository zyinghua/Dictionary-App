/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

import Messages.*;
import Utils.*;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DictionaryClient {
    public static final String ERROR_EMPTY_WORD = "Please enter a word.";
    public static final String ERROR_INVALID_WORD = "Word must not have any spaces, please try again.";
    public static final String ERROR_EMPTY_MEANING = "Please enter at least one meaning.";

    private static String promptOperation(Scanner sc)
    {
        while(true)
        {
            System.out.println("Input 'add' for adding a new word.");
            System.out.println("Input 'remove' for removing a word.");
            System.out.println("Input 'query' for querying a word.");
            System.out.println("Input 'update' for updating a word.");
            System.out.println("Input 'quit' to quit the program.");

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
        return word.matches(Utils.WORD_REGEX);
    }

    private static String promptWord(Scanner sc)
    {
        while(true)
        {
            System.out.println("Please enter the word: ");
            String word = sc.nextLine().toLowerCase();

            if (checkWordValidity(word))
            {
                return word;
            }
            else
            {
                System.out.println(ERROR_INVALID_WORD + "\n");
            }
        }
    }

    public static void checkServerValidity(String serverAddress, int serverPort)
    {
        Request request = new Request(Operation.ALIVE_MESSAGE, null);
        sendRequest(request, serverAddress, serverPort);
    }

    public static Response sendRequest(Request request, String serverAddress, int serverPort)
    {
        // Responsible for sending the request to the server and returning the response
        try
        {
            Socket client = new Socket(serverAddress, serverPort);

            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());

            oos.writeObject(request);
            oos.flush();

            Response response = (Response) ois.readObject();

            oos.close();
            ois.close();
            client.close();

            return response;

        } catch (ClassNotFoundException e) {
            String err = "[Error on parsing response] Class not found: " + e.getMessage();
            System.err.println(err);
            return new FailureResponse(Operation.UNKNOWN, err);
        }
        catch (NumberFormatException e) {
            System.out.println(e.getMessage() + "\nPort must be an integer.");
            System.exit(1);
        } catch (UnknownHostException e) {
            System.out.println("Cannot find the specified host. Please check host name: " + e.getMessage());
            System.exit(1);
        }
        catch (IOException e) {
            System.out.println("IO Exception encountered when connecting with the server: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + "\nPort must be between 0 and 65535");
            System.exit(1);
        }

        return new FailureResponse(Operation.UNKNOWN, "Unknown error when sending request.");
    }

    public static void main(String[] args){
        if (args.length != 2) {
            // Handle invalid number of arguments
            System.out.println("Usage: java -jar DictionaryClient.jar <server-address> <server-port>");
            System.exit(1);
        }


        checkServerValidity(args[0], Integer.parseInt(args[1]));

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

                Response response = sendRequest(request, args[0], Integer.parseInt(args[1]));

                if (response instanceof QueryResponse)
                {
                    QueryResponse queryResponse = (QueryResponse) response;
                    System.out.println("The meanings of the word are: ");
                    for (String meaning : queryResponse.getMeanings())
                    {
                        System.out.println(meaning);
                    }
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
}
