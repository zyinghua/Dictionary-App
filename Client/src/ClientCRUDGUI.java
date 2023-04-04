/*
    @author: Yinghua Zhou
    Student ID: 1308266
 */

import Messages.AddUpdateRequest;
import Messages.QueryResponse;
import Messages.Request;
import Messages.Response;
import Utils.Operation;
import Utils.Result;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ClientCRUDGUI extends JFrame {
    private Operation op;
    private static final String PROMPT_WORD = "Please enter the word: ";
    private static final String PROMPT_MEANINGS = "Please enter a meaning of the word: ";
    private static final String PROMPT_ADDITIONAL_MEANINGS = "[Optional] Please enter another meaning of the word: ";
    private final HintTextField inputField;
    private final JButton addMeaningButton;
    private final JButton confirmButton;
    private final JTextPane textPane;
    private StyledDocument textPaneDoc;
    Request request;
    private int state = 0;  // 0 = word not entered, 1 = word entered, 2+ = respective number - 1 of meanings entered
    private final Style redText, blackText, blueText, greyText;
    private static final int USER_INPUT = 0;
    private static final int SERVER_VALID_RESPONSE = 1;
    private static final int SERVER_ERROR_RESPONSE = 2;
    public ClientCRUDGUI(ClientMainGUI previousFrame, Operation op) {
        super(ClientMainGUI.FRAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(ClientMainGUI.FRAME_WIDTH, ClientMainGUI.FRAME_HEIGHT);

        this.op = op;
        initialiseRequest();

        JPanel panel = new JPanel(new BorderLayout());  // Main panel

        JButton backButton = new JButton("<- Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                previousFrame.setVisible(true);
                try {
                    previousFrame.preservedRequestHistory = textPaneDoc.getText(0, textPaneDoc.getLength());
                } catch (BadLocationException ex) {
                    JOptionPane.showMessageDialog(null, "[Internal Error] Operation at an invalid position " +
                            "when saving text from the screen. " + ex.getMessage(), "Bad Location Exception", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create the top section
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.insets = new Insets(5, 5, 10, 5);  // Spaces between a button and surrounding containers
        topPanel.add(backButton, gbcTop);

        // Create the middle section
        JPanel midPanel = new JPanel(new BorderLayout());
        this.textPane = new JTextPane();
        this.textPane.setEditable(false);

        this.textPaneDoc = this.textPane.getStyledDocument();
        // Create a style for each color
        this.redText = this.textPaneDoc.addStyle("red", null);
        StyleConstants.setForeground(this.redText, Color.RED);
        this.blackText = this.textPaneDoc.addStyle("black", null);
        StyleConstants.setForeground(this.blackText, Color.BLACK);
        this.blueText = this.textPaneDoc.addStyle("blue", null);
        StyleConstants.setForeground(this.blueText, Color.BLUE);
        this.greyText = this.textPaneDoc.addStyle("grey", null);
        StyleConstants.setForeground(this.greyText, Color.GRAY);

        try{
            this.textPaneDoc.insertString(0, previousFrame.preservedRequestHistory, greyText);
        } catch (BadLocationException e) {
            JOptionPane.showMessageDialog(null, "[Internal Error] Operation at an invalid position " +
                    "when loading history request text to the screen. " + e.getMessage(), "Bad Location Exception", JOptionPane.ERROR_MESSAGE);
        }

        midPanel.add(this.textPane, BorderLayout.CENTER);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(midPanel);

        // Create the bottom section
        JPanel inputPanel = new JPanel(new BorderLayout());
        JLabel inputLabel = new JLabel(getOperationHint(op));
        this.inputField = new HintTextField(PROMPT_WORD);

        this.addMeaningButton = new JButton("Save & Add Another Meaning");
        this.addMeaningButton.setVisible(false);
        this.addMeaningButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inputField.getForeground() == Color.gray || inputField.getText().equals(""))
                {
                    JOptionPane.showMessageDialog(null, "Please enter the meaning.", "Empty Meaning", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    ((AddUpdateRequest) request).addMeaning(inputField.getText());

                    appendTextToTextPane(USER_INPUT, "Meaning " + state + ": " + inputField.getText() + "\n");
                    resetJTextFieldPrompt(++state);
                }
            }
        });

        inputPanel.add(inputLabel, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(addMeaningButton, BorderLayout.EAST);

        // Create the confirm button panel
        JPanel confirmPanel = new JPanel(new GridBagLayout());
        this.confirmButton = new JButton(op == Operation.ADD_WORD || op == Operation.UPDATE_WORD ? "Next" : "Confirm");
        this.confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (state == 0)
                {
                    // Check if the word is valid
                    if (DictionaryClient.checkWordValidity(inputField.getText()))
                    {
                        String word = inputField.getText();
                        appendTextToTextPane(USER_INPUT, "Word: " + word + "\n");
                        request.setWord(word);

                        if(op == Operation.ADD_WORD || op == Operation.UPDATE_WORD)
                        {
                            resetJTextFieldPrompt(++state);
                            addMeaningButton.setVisible(true);
                            confirmButton.setText("Confirm");
                        }
                        else
                        {
                            // Ready to send request
                            Response response = DictionaryClient.sendRequest(true, request, previousFrame.serverAddress, previousFrame.serverPort);
                            resetAllToInitialState(); // Clear the data relevant to the previous operation which the response has been received
                            handleResponse(response);
                        }
                    }
                    else if(inputField.getForeground() == Color.gray || inputField.getText().equals(""))
                    {
                        JOptionPane.showMessageDialog(null, DictionaryClient.ERROR_EMPTY_WORD, "Empty Word", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, DictionaryClient.ERROR_INVALID_WORD, "Invalid Word", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else if(state == 1)
                {
                    if(inputField.getForeground() == Color.gray || inputField.getText().equals(""))
                    {
                        JOptionPane.showMessageDialog(null, DictionaryClient.ERROR_EMPTY_MEANING, "Empty Meaning", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        // Ready to send request
                        ((AddUpdateRequest) request).addMeaning(inputField.getText());
                        appendTextToTextPane(USER_INPUT, "Meaning " + state + ": " + inputField.getText() + "\n");

                        Response response = DictionaryClient.sendRequest(true, request, previousFrame.serverAddress, previousFrame.serverPort);
                        resetAllToInitialState(); // Clear the data relevant to the previous operation which the response has been received
                        handleResponse(response);
                    }
                }
                else
                {
                    // Ready to send request

                    if(inputField.getForeground() != Color.gray && !inputField.getText().equals(""))
                    {
                        // If another meaning available in the text field, add it to the request
                        ((AddUpdateRequest) request).addMeaning(inputField.getText());
                        appendTextToTextPane(USER_INPUT, "Meaning " + state + ": " + inputField.getText() + "\n");
                    }

                    Response response = DictionaryClient.sendRequest(true, request, previousFrame.serverAddress, previousFrame.serverPort);
                    resetAllToInitialState(); // Clear the data relevant to the previous operation which the response has been received
                    handleResponse(response);
                }

            }
        });

        GridBagConstraints gbcBtm = new GridBagConstraints();
        gbcBtm.insets = new Insets(0, 5, 0, 5);  // Spaces between a button and surrounding containers
        confirmPanel.add(confirmButton, gbcBtm);

        // Add the input panel and confirm panel to the bottom section
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        bottomPanel.add(confirmPanel, BorderLayout.EAST);

        // Add the top panel and bottom panel to the main panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        setResizable(ClientMainGUI.RESIZABLE);
        setVisible(true);
        requestFocusInWindow();
    }

    public static class HintTextField extends JTextField{
        private String hint;

        public HintTextField(String hint) {
            this.hint = hint;
            setForeground(Color.gray);
            setText(hint);
            this.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (getForeground() == Color.gray) {
                        setForeground(Color.black);
                        setText("");
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setForeground(Color.gray);
                        setText(hint);
                    }
                }
            });
        }

        public void resetHint(String hint) {
            this.hint = hint;
            setForeground(Color.gray);
            setText(hint);
        }
    }

    private String getOperationHint(Operation op) {
        return switch (op) {
            case ADD_WORD -> "Add a new word: ";
            case REMOVE_WORD -> "Remove a word: ";
            case QUERY_WORD -> "Query a word: ";
            case UPDATE_WORD -> "Update a word: ";
            default -> "Unknown operation ";
        };
    }

    public void resetJTextFieldPrompt(int state)
    {
        if (state == 0)
        {
            // Reset the prompt
            this.inputField.resetHint(PROMPT_WORD);
        }
        else if (state == 1)
        {
            // Reset the prompt
            this.inputField.resetHint(PROMPT_MEANINGS);
        }
        else if (state >= 2)
        {
            // Reset the prompt
            this.inputField.resetHint(PROMPT_ADDITIONAL_MEANINGS);
        }
    }

    public void resetAllToInitialState()
    {
        this.state = 0;
        resetJTextFieldPrompt(this.state); // Reset the prompt
        initialiseRequest(); // Reset the request

        this.confirmButton.setText(op == Operation.ADD_WORD || op == Operation.UPDATE_WORD ? "Next" : "Confirm"); // Reset the confirm button text
        this.addMeaningButton.setVisible(false); // Hide the add meaning button
    }

    private void initialiseRequest()
    {
        if (this.op == Operation.ADD_WORD || this.op == Operation.UPDATE_WORD)
        {
            this.request = new AddUpdateRequest(this.op);
        }
        else
        {
            this.request = new Request(this.op);
        }
    }

    private void handleResponse(Response response)
    {
        if (response.getStatus() == Result.SUCCESS)
        {
            if (response instanceof QueryResponse)
            {
                QueryResponse queryResponse = (QueryResponse) response;
                appendTextToTextPane(SERVER_VALID_RESPONSE, "The meanings of the word are: ");
                for (int i = 0; i < queryResponse.getMeanings().size(); i++)
                {
                    appendTextToTextPane(SERVER_VALID_RESPONSE, "\nMeaning " + (i + 1) + ": " + queryResponse.getMeanings().get(i));
                }
                appendTextToTextPane(SERVER_VALID_RESPONSE, "\n\n");
            } else {
                appendTextToTextPane(SERVER_VALID_RESPONSE, response + "\n\n");
            }
        }
        else
        {
            appendTextToTextPane(SERVER_ERROR_RESPONSE, response + "\n\n");
        }
    }

    private void appendTextToTextPane(int property, String text)
    {
        try
        {
            this.textPaneDoc.insertString(this.textPaneDoc.getLength(), text, switch (property) {
                case USER_INPUT -> this.blueText; // User input
                case SERVER_VALID_RESPONSE -> this.blackText; // Server valid response
                case SERVER_ERROR_RESPONSE -> this.redText; // Server error response
                default -> null;
            });
        } catch (BadLocationException e)
        {
            JOptionPane.showMessageDialog(null, "[Internal Error] Operation at an invalid position " +
                    "when inserting text to the screen. "  + e.getMessage(), "Bad Location Exception", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "[Internal Error]: " + e.getMessage() + " when inserting text to the screen.", "Unknown Exception", JOptionPane.ERROR_MESSAGE);
        }
    }
}
