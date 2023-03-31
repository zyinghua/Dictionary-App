/*
    @author: Yinghua Zhou
    Student ID: 1308266
 */

import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame {
    private static final boolean RESIZABLE = true;
    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 800;
    private static final int CRUD_BTN_WIDTH = 400;
    private static final int CRUD_BTN_HEIGHT = 100;
    private static final int CRUD_BTN_FONT_SIZE = 20;
    private static final String CRUD_BTN_FONT_NAME = "Arial";
    private static final int CRUD_BTN_FONT_STYLE = Font.BOLD + Font.ITALIC;
    private static final String ADD_BTN_TEXT = "Add a word";
    private static final String REMOVE_BTN_TEXT = "Remove a word";
    private static final String QUERY_BTN_TEXT = "Query a word";
    private static final String UPDATE_BTN_TEXT = "Update a word";

    private JLabel headingLabel;
    private JButton addButton, removeButton, queryButton, updateButton;

    public ClientGUI() {
        setTitle("Dictionary App Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
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
        panel.add(this.queryButton, gbc);

        gbc.gridy++;  // Move to the next row
        this.addButton = new JButton(ADD_BTN_TEXT);
        this.addButton.setPreferredSize(new Dimension(CRUD_BTN_WIDTH, CRUD_BTN_HEIGHT));
        this.addButton.setFont(new Font(CRUD_BTN_FONT_NAME, CRUD_BTN_FONT_STYLE, CRUD_BTN_FONT_SIZE));
        panel.add(this.addButton, gbc);

        gbc.gridy++;  // Move to the next row
        this.updateButton = new JButton(UPDATE_BTN_TEXT);
        this.updateButton.setPreferredSize(new Dimension(CRUD_BTN_WIDTH, CRUD_BTN_HEIGHT));
        this.updateButton.setFont(new Font(CRUD_BTN_FONT_NAME, CRUD_BTN_FONT_STYLE, CRUD_BTN_FONT_SIZE));
        panel.add(this.updateButton, gbc);

        gbc.gridy++;  // Move to the next row
        this.removeButton = new JButton(REMOVE_BTN_TEXT);
        this.removeButton.setPreferredSize(new Dimension(CRUD_BTN_WIDTH, CRUD_BTN_HEIGHT));
        this.removeButton.setFont(new Font(CRUD_BTN_FONT_NAME, CRUD_BTN_FONT_STYLE, CRUD_BTN_FONT_SIZE));
        panel.add(this.removeButton, gbc);

        setContentPane(panel);
        setResizable(RESIZABLE);
        setVisible(true);
        getContentPane().setBackground(new Color(253, 253, 253));
    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}
