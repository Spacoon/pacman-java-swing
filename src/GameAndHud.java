import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameAndHud extends JFrame {

    public GameAndHud(LevelType id) {
        setLayout(new BorderLayout(0, 0));

        Game game = new Game(id);

        JPanel hud = new JPanel();
        hud.setBackground(Color.BLACK);
        hud.setPreferredSize(new Dimension(100, 60));



        JLabel scoreLabel = new JLabel("Score: " + game.player.getScore());
        scoreLabel.setForeground(Color.WHITE);

        JLabel livesLabel = new JLabel("Lives: " + game.player.getHealth());
        livesLabel.setForeground(Color.WHITE);

        JLabel timerLabel = new JLabel("Time: " + game.getElapsedSeconds() + "s");
        timerLabel.setForeground(Color.WHITE);

        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        livesLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 30));

        hud.add(scoreLabel);
        hud.add(livesLabel);
        hud.add(timerLabel);

        new Thread(() -> {
            while (game.isRunning()) {
                try {
                    Thread.sleep(100);
                    scoreLabel.setText("Score: " + game.player.getScore());
                    livesLabel.setText("Lives: " + game.player.getHealth());
                    timerLabel.setText("Time: " + game.getElapsedSeconds() + "s");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (game.isRunning()) {
                try {
                    Thread.sleep(10);

                    if (game.isGameWon()) {
                        game.setRunning(false);
                        game.setGameWon(false);
                        SwingUtilities.invokeLater(() -> {
                            WinnerWindow winnerWindow = new WinnerWindow(game.player.getScore());
                            winnerWindow.setVisible(true);
                            dispose();
                        });
                    }
                    if (game.isGameOver()) {
                        game.setRunning(false);

                        GameOverWindow gameOverWindow = new GameOverWindow();
                        gameOverWindow.setVisible(true);
                        dispose();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();


        add(hud, BorderLayout.NORTH);

        add(game, BorderLayout.CENTER);

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        game.getPlayer().setDesiredDirection(Direction.UP);
                        break;
                    case KeyEvent.VK_DOWN:
                        game.getPlayer().setDesiredDirection(Direction.DOWN);
                        break;
                    case KeyEvent.VK_LEFT:
                        game.getPlayer().setDesiredDirection(Direction.LEFT);
                        break;
                    case KeyEvent.VK_RIGHT:
                        game.getPlayer().setDesiredDirection(Direction.RIGHT);
                        break;
                    case KeyEvent.VK_ESCAPE:
                        game.setRunning(false);
                        new Menu();

                        dispose();
                        break;
                }
            }
        });

        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("PAC-MAN");
        setResizable(true);
        setVisible(true);
    }
}
