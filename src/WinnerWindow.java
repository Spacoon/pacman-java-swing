import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class WinnerWindow extends JFrame {
    public WinnerWindow(int score) {
        super("Winner");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBounds(100, 150, 250, 50);
        panel.setBackground(Color.BLUE);

        JLabel title = new JLabel("You won");
        title.setFont(new Font("Arial", Font.BOLD, 50));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(new EmptyBorder(10, 10, 10, 10));
        title.setForeground(Color.YELLOW);

        panel.add(title, BorderLayout.NORTH);

        JTextField textField = new JTextField("Your name");
        textField.setPreferredSize(new Dimension(100,20));

        panel.add(textField, BorderLayout.CENTER);

        JButton submitButton = new JButton("Submit");

        submitButton.setFont(new Font("Arial", Font.PLAIN, 30));
        submitButton.setBackground(Color.BLACK);
        submitButton.setForeground(Color.YELLOW);
        submitButton.addActionListener(e -> {
            String playerName = textField.getText();

            Menu.highScores.put(score, playerName);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream("scoreData.txt");
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(Menu.highScores);
                objectOutputStream.close();
                new Menu();
                dispose();

            } catch (IOException f) {
                f.printStackTrace();
            }
        });

        submitButton.setBorderPainted(false);
        panel.add(submitButton, BorderLayout.SOUTH);

        add(panel);
        setSize(200, 100);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}