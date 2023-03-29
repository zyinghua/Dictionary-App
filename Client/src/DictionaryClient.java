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
            System.out.println("Input '1' for adding a new word.");
            System.out.println("Input '2' for removing a word.");
            System.out.println("Input '3' for querying a word.");
            System.out.println("Input '4' for updating a word.");
            System.out.println("Input 'q' to quit the program.");

            System.out.println("Please select an operation from above: ");
            String op = sc.nextLine();

            if (List.of("1", "2", "3", "4", "q").contains(op))
            {
                return op;
            }
            else
            {
                System.out.println("The input is not acceptable, please re-enter.");
            }
        }
    }

    public static void main(String[] args){
        if (args.length != 2) {
            // Handle invalid number of arguments
            System.out.println("Usage: java -jar DictionaryClient.jar <server-address> <server-port>");
            System.exit(1);
        }

        try (Socket client = new Socket(args[0], Integer.parseInt(args[1])))
        {
            InputStream inputStream = client.getInputStream();
            OutputStream outputStream = client.getOutputStream();

            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());

            Scanner sc = new Scanner(System.in);
            boolean done = false;

            while(!done)
            {
                Request request = null;

                String op = promptOperation(sc);

                switch (op) {
                    case "1" -> {
                        System.out.println("Please enter the new word: ");
                        String wordToAdd = sc.nextLine();
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
                    case "2" -> {
                        System.out.println("Please enter the word to be removed: ");
                        String wordToRemove = sc.nextLine();
                        request = new Request(Operation.REMOVE_WORD, wordToRemove);
                    }
                    case "3" -> {
                        System.out.println("Please enter the word to query: ");
                        String wordToQuery = sc.nextLine();
                        request = new Request(Operation.QUERY_WORD, wordToQuery);
                    }
                    case "4" -> {
                        System.out.println("Please enter the word to be updated: ");
                        String wordToUpdate = sc.nextLine();
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
                    case "q" -> done = true;
                    default -> request = new Request();
                }

                if (request != null) {
                    oos.writeObject(request);
                    oos.flush();
                }

                if (!done) {
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
                }
            }

            oos.close();
            ois.close();
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage() + "\nPort must be an integer.");
            System.exit(1);
        } catch (UnknownHostException e) {
            System.out.println("Cannot find the specified host. Please check host name: " + e.getMessage());
            System.exit(1);
        }
        catch (IOException e) {
            System.out.println("IO Exception encountered on starting up the client: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + "\nPort must be between 0 and 65535");
            System.exit(1);
        }
    }
}
