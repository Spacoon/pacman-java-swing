import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GameOverWindow extends JFrame {
    public GameOverWindow() {
        super("GAME OVER");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(250, 50);
        panel.setBackground(Color.BLUE);

        JLabel title = new JLabel("GAME OVER");
        title.setFont(new Font("Arial", Font.BOLD, 50));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(new EmptyBorder(10, 10, 10, 10));
        title.setForeground(Color.YELLOW);

        panel.add(title, BorderLayout.NORTH);

        JButton menuButton = new JButton("Main Menu");
        menuButton.setFont(new Font("Arial", Font.PLAIN, 30));
        menuButton.setBackground(Color.BLACK);
        menuButton.setForeground(Color.YELLOW);
        menuButton.addActionListener(e -> {
            dispose();
            new Menu();
        });
        menuButton.setBorderPainted(false);


        panel.add(menuButton, BorderLayout.CENTER);


        add(panel);
        setSize(200, 100);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
