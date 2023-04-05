/*
    @Author: Yinghua Zhou
    Student ID: 1308266
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerMainGUI extends JFrame {
    private String FRAME_TITLE = "Dictionary Server";

    public static final boolean RESIZABLE = true;
    public static final int FRAME_WIDTH = 750;
    public static final int FRAME_HEIGHT = 600;
    private static final int FONT_SIZE = 20;

    private static final int BTN_WIDTH = 400;
    private static final int BTN_HEIGHT = 100;
    private static final String FONT_NAME = "Arial";
    private static final int FONT_STYLE = Font.BOLD + Font.ITALIC;
    private JLabel threadCountLabel;
    private JLabel requestCountLabel;

    private JLabel threadCountValue;
    private JLabel requestCountValue;
    private WorkerPoolManager workerPoolManager;
    private AtomicBoolean shouldTerminate;
    private int threadCount;
    private JButton quitBtn;
    private JButton checkDictBtn;
    private JButton saveBtn;

    public ServerMainGUI(int threadCount, AtomicBoolean shouldTerminate, WorkerPoolManager workerPoolManager) {
        setTitle(FRAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);

        this.shouldTerminate = shouldTerminate;
        this.workerPoolManager = workerPoolManager;

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;  // initial x
        gbc.gridy = 0;  // initial y
        gbc.insets = new Insets(20, 5, 20, 5);  // Spaces between a button and surrounding containers


        threadCountLabel = new JLabel("Number of Active Threads: ");
        requestCountLabel = new JLabel("Number of Requests Processed: ");

        this.threadCount = threadCount;
        threadCountValue = new JLabel(String.valueOf(threadCount));
        requestCountValue = new JLabel(String.valueOf(workerPoolManager.getNumRequestsProcessed().get()));

        threadCountLabel.setFont(new Font(FONT_NAME, FONT_STYLE, FONT_SIZE));
        requestCountLabel.setFont(new Font(FONT_NAME, FONT_STYLE, FONT_SIZE));
        threadCountValue.setFont(new Font(FONT_NAME, FONT_STYLE, FONT_SIZE));
        requestCountValue.setFont(new Font(FONT_NAME, FONT_STYLE, FONT_SIZE));

        checkDictBtn = new JButton("Check Dictionary");
        this.checkDictBtn.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));

        quitBtn = new JButton("Quit the server");
        this.quitBtn.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));

        saveBtn = new JButton("Save Dictionary");
        this.saveBtn.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));

        panel.add(threadCountLabel, gbc);

        gbc.gridx++;
        panel.add(threadCountValue, gbc);

        gbc.gridx--;
        gbc.gridy++;
        panel.add(requestCountLabel, gbc);

        gbc.gridx++;
        panel.add(requestCountValue, gbc);

        gbc.gridx--;
        gbc.gridy++;
        panel.add(checkDictBtn, gbc);

        gbc.gridy++;
        panel.add(saveBtn, gbc);

        gbc.gridy++;
        panel.add(quitBtn, gbc);

        add(panel);

        quitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle Quit button click event
                shouldTerminate.set(true);
                System.out.println("Server is shutting down...");
                dispose();
            }
        });

        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle Save button click event
                new Thread(workerPoolManager.getDict()::writeDictDataToFile).start();
            }
        });

        checkDictBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle Check Dictionary button click event
            }
        });


        setContentPane(panel);
        setResizable(RESIZABLE);
        setVisible(true);
        getContentPane().setBackground(new Color(253, 253, 253));
        requestFocusInWindow();
    }
}
