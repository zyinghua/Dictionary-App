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

    private static String promptWord(Scanner sc)
    {
        while(true)
        {
            System.out.println("Please enter the word: ");
            String word = sc.nextLine().toLowerCase();

            if (word.matches(Utils.WORD_REGEX))
            {
                return word;
            }
            else
            {
                System.out.println("Word must not have any spaces, please try again.\n");
            }
        }
    }

    public static void main(String[] args){
        if (args.length != 2) {
            // Handle invalid number of arguments
            System.out.println("Usage: java -jar DictionaryClient.jar <server-address> <server-port>");
            System.exit(1);
        }

        try
        {
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
                            System.out.println("Add a meaning for the word, or press enter to conclude: ");
                            String input = sc.nextLine();

                            if (!input.isEmpty()) {
                                meanings.add(input);
                            } else {
                                break;
                            }
                        }
                        request = new AddUpdateRequest(Operation.ADD_WORD, wordToAdd, meanings);
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
                            System.out.println("Add a meaning for the word, or press enter to conclude: ");
                            String input = sc.nextLine();

                            if (!input.isEmpty()) {
                                newMeanings.add(input);
                            } else {
                                break;
                            }
                        }
                        request = new AddUpdateRequest(Operation.UPDATE_WORD, wordToUpdate, newMeanings);
                    }
                    case "quit" -> done = true;
                }

                if (!done && request != null) {
                    Socket client = new Socket(args[0], Integer.parseInt(args[1]));

                    ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                    ObjectInputStream ois = new ObjectInputStream(client.getInputStream());

                    oos.writeObject(request);
                    oos.flush();

                    try {
                        Response response = (Response) ois.readObject();

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

                    } catch (ClassNotFoundException e) {
                        System.err.println("[Error] Class not found: " + e.getMessage());
                    }

                    oos.close();
                    ois.close();
                    client.close();
                } else if(!done) {
                    System.err.println("Invalid request. Please try again...\n");
                } else {
                    System.out.println("Quitting the program...");
                }
            }
        } catch (NumberFormatException e) {
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
    }
}
