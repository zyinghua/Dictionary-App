import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class test extends JFrame {
    private JTextPane textPane;

    public test() {
        textPane = new JTextPane();
        textPane.setEditable(false);
        add(new JScrollPane(textPane));

        // Create a styled document
        StyledDocument doc = textPane.getStyledDocument();

        // Create a style for each color
        Style red = doc.addStyle("red", null);
        StyleConstants.setForeground(red, Color.RED);
        Style green = doc.addStyle("green", null);
        StyleConstants.setForeground(green, Color.GREEN);
        Style blue = doc.addStyle("blue", null);
        StyleConstants.setForeground(blue, Color.BLUE);

        // Insert text with different colors
        try {
            doc.insertString(doc.getLength(), "This is a ", null);
            doc.insertString(doc.getLength(), "red", red);
            doc.insertString(doc.getLength(), " text, this is a ", null);
            doc.insertString(doc.getLength(), "green", green);
            doc.insertString(doc.getLength(), " text, and this is a ", null);
            doc.insertString(doc.getLength(), "blue", blue);
            doc.insertString(doc.getLength(), " text.", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        // Set the size and visibility of the JFrame
        setSize(400, 300);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new test();
    }
}