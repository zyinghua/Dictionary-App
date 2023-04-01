/*
    @author: Yinghua Zhou
    Student ID: 1308266

    This class is the first window of the client App,
    on top of showing up the link to different operations,
    it firstly attempts to send an alive message to the server
    to check if the server's availability and the validatiy of
    parameters.
 */

import Messages.FailureResponse;
import Messages.Response;
import Utils.Operation;
import Utils.Result;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientMainGUI extends JFrame{
    public static final String FRAME_TITLE = "Dictionary App Client";
    public static final boolean RESIZABLE = true;
    public static final int FRAME_WIDTH = 1000;
    public static final int FRAME_HEIGHT = 800;
    private static final int CRUD_BTN_WIDTH = 400;
    private static final int CRUD_BTN_HEIGHT = 100;
    private static final int CRUD_BTN_FONT_SIZE = 20;
    private static final String CRUD_BTN_FONT_NAME = "Arial";
    private static final int CRUD_BTN_FONT_STYLE = Font.BOLD + Font.ITALIC;
    private static final String ADD_BTN_TEXT = "Add a word";
    private static final String REMOVE_BTN_TEXT = "Remove a word";
    private static final String QUERY_BTN_TEXT = "Query a word";
    private static final String UPDATE_BTN_TEXT = "Update a word";

    public String serverAddress;
    public int serverPort;

    private JPanel panel;
    private JLabel headingLabel;
    private JButton addButton, removeButton, queryButton, updateButton;

    public ClientMainGUI(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        setTitle(FRAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);

        this.panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;  // initial x
        gbc.gridy = 0;  // initial y
        gbc.insets = new Insets(20, 10, 20, 10);  // Spaces between a button and surrounding containers

        this.headingLabel = new JLabel("Welcome to the Dictionary Application!");
        this.headingLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
        this.headingLabel.setForeground(Color.BLUE);
        panel.add(this.headingLabel, gbc);

        gbc.gridy++;  // Move to the next row
        this.queryButton = new JButton(QUERY_BTN_TEXT);
        this.queryButton.setPreferredSize(new Dimension(CRUD_BTN_WIDTH, CRUD_BTN_HEIGHT));
        this.queryButton.setFont(new Font(CRUD_BTN_FONT_NAME, CRUD_BTN_FONT_STYLE, CRUD_BTN_FONT_SIZE));
        this.queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClientCRUDGUI((ClientMainGUI) SwingUtilities.getWindowAncestor(queryButton), Operation.QUERY_WORD);
                setVisible(false);
            }
        });
        panel.add(this.queryButton, gbc);

        gbc.gridy++;  // Move to the next row
        this.addButton = new JButton(ADD_BTN_TEXT);
        this.addButton.setPreferredSize(new Dimension(CRUD_BTN_WIDTH, CRUD_BTN_HEIGHT));
        this.addButton.setFont(new Font(CRUD_BTN_FONT_NAME, CRUD_BTN_FONT_STYLE, CRUD_BTN_FONT_SIZE));
        this.addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClientCRUDGUI((ClientMainGUI)SwingUtilities.getWindowAncestor(addButton), Operation.ADD_WORD);
                setVisible(false);
            }
        });
        panel.add(this.addButton, gbc);

        gbc.gridy++;  // Move to the next row
        this.updateButton = new JButton(UPDATE_BTN_TEXT);
        this.updateButton.setPreferredSize(new Dimension(CRUD_BTN_WIDTH, CRUD_BTN_HEIGHT));
        this.updateButton.setFont(new Font(CRUD_BTN_FONT_NAME, CRUD_BTN_FONT_STYLE, CRUD_BTN_FONT_SIZE));
        this.updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClientCRUDGUI((ClientMainGUI)SwingUtilities.getWindowAncestor(updateButton), Operation.UPDATE_WORD);
                setVisible(false);
            }
        });
        panel.add(this.updateButton, gbc);

        gbc.gridy++;  // Move to the next row
        this.removeButton = new JButton(REMOVE_BTN_TEXT);
        this.removeButton.setPreferredSize(new Dimension(CRUD_BTN_WIDTH, CRUD_BTN_HEIGHT));
        this.removeButton.setFont(new Font(CRUD_BTN_FONT_NAME, CRUD_BTN_FONT_STYLE, CRUD_BTN_FONT_SIZE));
        this.removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClientCRUDGUI((ClientMainGUI)SwingUtilities.getWindowAncestor(removeButton), Operation.REMOVE_WORD);
                setVisible(false);
            }
        });
        panel.add(this.removeButton, gbc);

        setContentPane(panel);
        setResizable(RESIZABLE);
        setVisible(true);
        getContentPane().setBackground(new Color(253, 253, 253));
        requestFocusInWindow();
        closeIfServerConnectionFail(serverAddress, serverPort);
    }

    private void closeIfServerConnectionFail(String serverAddress, int serverPort)
    {
        Response response = DictionaryClient.checkServerValidity(true, serverAddress, serverPort);

        if (response.getStatus() == Result.FAILURE)
        {
            JOptionPane.showMessageDialog(this, ((FailureResponse)response).getMessage());
            System.exit(1);
        }
    }
}
