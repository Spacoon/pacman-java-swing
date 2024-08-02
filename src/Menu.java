import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class Menu extends JFrame {
    static Map<Integer, String> highScores;

    public Menu() {
        super("MENU");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        highScores = new TreeMap<>(Collections.reverseOrder());

//        https://www.baeldung.com/java-serialization
        File file = new File("scoreData.txt");
        if (file.length() != 0) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                Map<Integer, String> map = (Map<Integer, String>) (objectInputStream.readObject());

                highScores.putAll(map);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 20 ));
        panel.setBounds(200, 100, 250, 100);

        JLabel title = new JLabel("PACMAN");

        JButton newGameButton = new JButton("New Game");
        JButton highScoresButton = new JButton("High Scores");
        JButton exitButton = new JButton("Exit");

        newGameButton.setFont(new Font("Arial", Font.PLAIN, 35));
        highScoresButton.setFont(new Font("Arial", Font.PLAIN, 35));
        exitButton.setFont(new Font("Arial", Font.PLAIN, 35));

        title.setFont(new Font("Arial", Font.BOLD, 45));

        title.setHorizontalAlignment(SwingConstants.CENTER);

        newGameButton.setBackground(Color.BLACK);
        highScoresButton.setBackground(Color.BLACK);
        exitButton.setBackground(Color.BLACK);

        newGameButton.setForeground(Color.YELLOW);
        highScoresButton.setForeground(Color.YELLOW);
        exitButton.setForeground(Color.YELLOW);

        newGameButton.setBorderPainted(false);
        highScoresButton.setBorderPainted(false);
        exitButton.setBorderPainted(false);

        title.setForeground(Color.YELLOW);

        panel.setBackground(Color.BLUE);

        newGameButton.addActionListener(e -> {
            LevelSelector lvl = new LevelSelector();
            if (!lvl.isCancelled())
                dispose();
        });
        highScoresButton.addActionListener(e -> {
            new HighScores();
            dispose();
        });

        exitButton.addActionListener(e -> System.exit(0));

        panel.add(title);
        panel.add(newGameButton);
        panel.add(highScoresButton);
        panel.add(exitButton);

        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(panel);
        pack();
        setSize(500, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}