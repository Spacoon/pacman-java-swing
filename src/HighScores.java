import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class HighScores extends JFrame {
    public HighScores() {
        super("High Scores");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Map<Integer, String> highScores = Menu.highScores;

        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(500, 700);
        panel.setBackground(Color.BLUE);

        JLabel title = new JLabel("LEADERBOARD");
        title.setFont(new Font("Arial", Font.BOLD, 50));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(new EmptyBorder(10, 10, 10, 10));
        title.setForeground(Color.YELLOW);

        panel.add(title, BorderLayout.NORTH);

        JList<String> list = new JList<>();
        list.setFont(list.getFont().deriveFont(30.0f));

        String[] highScoresArray = new String[highScores.size()];
        int index = 0;
        for (Map.Entry<Integer, String> entry : highScores.entrySet()) {
            highScoresArray[index++] = entry.getKey() + " - " + entry.getValue();
        }

        list.setListData(highScoresArray);
        list.setBackground(Color.BLACK);
        list.setForeground(Color.YELLOW);
        list.setSelectionBackground(Color.RED);
        list.setSelectionForeground(Color.BLUE);
        list.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        scrollPane.setBackground(Color.BLUE);

        JButton backButton = new JButton("Main Menu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 30));
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.YELLOW);
        backButton.addActionListener(e -> {
            dispose();
            new Menu();
        });
        backButton.setBorderPainted(false);

        panel.add(backButton, BorderLayout.SOUTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        pack();
        setSize(500, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
