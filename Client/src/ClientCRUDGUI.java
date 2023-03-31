/*
    @author: Yinghua Zhou
    Student ID: 1308266
 */

import Utils.Operation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;

public class ClientCRUDGUI extends JFrame {
    private static final String PROMPT_WORD = "Please enter the word: ";
    private static final String PROMPT_MEANINGS = "Please enter a meaning of the word: ";
    private static final String PROMPT_ADDITIONAL_MEANINGS = "[Optional] Please enter another meaning of the word: ";
    private final HintTextField inputField;
    private final JButton addMeaningButton;
    private String ConfirmBtnText;

    private int state = 0;  // 0 = word not entered, 1 = word entered, 2+ = respective number - 1 of meanings entered
    public ClientCRUDGUI(JFrame previousFrame, Operation op) {
        super(ClientMainGUI.FRAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(ClientMainGUI.FRAME_WIDTH, ClientMainGUI.FRAME_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout());  // Main panel

        JButton backButton = new JButton("<- Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                previousFrame.setVisible(true);
            }
        });

        // Create the top section
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.insets = new Insets(5, 5, 10, 5);  // Spaces between a button and surrounding containers
        topPanel.add(backButton, gbcTop);

        // Create the middle section
        JPanel midPanel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        midPanel.add(textArea, BorderLayout.CENTER);
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
                if (inputField.getText().equals(PROMPT_MEANINGS) || inputField.getText().equals(""))
                {
                    JOptionPane.showMessageDialog(null, "Please enter the meaning.", "Empty Meaning", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    textArea.append("Meaning " + state + ": " + inputField.getText() + "\n");
                    resetJTextFieldPrompt(++state);
                    inputField.resetHint(PROMPT_ADDITIONAL_MEANINGS);
                }
            }
        });

        inputPanel.add(inputLabel, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(addMeaningButton, BorderLayout.EAST);

        // Create the confirm button panel
        JPanel confirmPanel = new JPanel(new GridBagLayout());
        JButton confirmButton = new JButton(op == Operation.ADD_WORD || op == Operation.UPDATE_WORD ? "Next" : "Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (state == 0)
                {
                    if (DictionaryClient.checkWordValidity(inputField.getText()))
                    {
                        textArea.append("Word: " + inputField.getText() + "\n");

                        if(op == Operation.ADD_WORD || op == Operation.UPDATE_WORD)
                        {
                            resetJTextFieldPrompt(++state);
                            addMeaningButton.setVisible(true);
                            confirmButton.setText("Confirm");
                            inputField.resetHint(PROMPT_MEANINGS);
                        }
                        else
                        {
                            // Ready to send request
                        }
                    }
                    else if(inputField.getText().equals(PROMPT_WORD) || inputField.getText().equals(""))
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
                    if(inputField.getText().equals(PROMPT_MEANINGS) || inputField.getText().equals(""))
                    {
                        JOptionPane.showMessageDialog(null, DictionaryClient.ERROR_EMPTY_MEANING, "Empty Meaning", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        // Ready to send request
                        textArea.append("Meaning: " + inputField.getText() + "\n");
                    }
                }
                else
                {
                    // Ready to send request
                    textArea.append("Meaning " + state + ": " + inputField.getText() + "\n");

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
        switch (op) {
            case ADD_WORD:
                return "Add a new word: ";
            case REMOVE_WORD:
                return "Remove a word: ";
            case QUERY_WORD:
                return "Query a word: ";
            case UPDATE_WORD:
                return "Update a word: ";
            default:
                return "Unknown operation ";
        }
    }

    public void resetJTextFieldPrompt(int state)
    {
        if (state == 0)
        {
            // Reset the prompt
            this.inputField.setText(PROMPT_WORD);
        }
        else if (state == 1)
        {
            // Reset the prompt
            this.inputField.setText(PROMPT_MEANINGS);
        }
        else if (state == 2)
        {
            // Reset the prompt
            this.inputField.setText(PROMPT_ADDITIONAL_MEANINGS);
        }
    }
}
