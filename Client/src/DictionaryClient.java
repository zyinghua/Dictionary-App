package Client;
import Messages.*;
import Utils.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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

    public static void main(String[] args) throws IOException {
        Socket client = new Socket("localhost", 8888);

        DataOutputStream dos = new DataOutputStream(client.getOutputStream());
        DataInputStream dis = new DataInputStream(client.getInputStream());

        Scanner sc = new Scanner(System.in);
        boolean done = false;

        while(!done)
        {
            Request request = new Request();

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
            }

            dos.writeUTF(Utils.encodeRequest(request));
            dos.flush();

            if (!done) System.out.println(dis.readUTF());
        }

        dos.close();
        dis.close();
        client.close();
    }
}
