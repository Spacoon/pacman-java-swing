import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class Game extends JPanel {

    private String[] map;
    private final LevelType levelType;
    private int elapsedSeconds = 0;
    private boolean isRunning = true;
    private final Object lock = new Object();
    private boolean isGameWon = false;
    private boolean isGameOver = false;

    final PowerUp[] powerUps = new PowerUp[] {
            new DoublePlayerScorePowerUp(),
            new EatGhostsPowerUp(),
            new DoubleScoreRatePowerUp(),
            new SpeedPowerUp(),
            new IncrementHealthPowerUp()
    };

    final String[] playerAnimationDown = new String[] {
            "assets/pacman-down/1.png",
            "assets/pacman-down/2.png",
            "assets/pacman-down/3.png"
    };
    final String[] playerAnimationLeft = new String[] {
            "assets/pacman-left/1.png",
            "assets/pacman-left/2.png",
            "assets/pacman-left/3.png"
    };
    final String[] playerAnimationRight = new String[] {
            "assets/pacman-right/1.png",
            "assets/pacman-right/2.png",
            "assets/pacman-right/3.png"
    };
    final String[] playerAnimationUp = new String[] {
            "assets/pacman-up/1.png",
            "assets/pacman-up/2.png",
            "assets/pacman-up/3.png"
    };

    private boolean isSpeedPowerUp = false;
    private boolean isAbleToEatGhosts = false;
    private boolean isDoubleScoreRate = false;

    Player player = new Player(1, 1, playerAnimationRight[0]);
    Ghost[] ghosts =  {
            new Ghost(1, 1, "assets/ghosts/blinky.png"),
            new Ghost(1, 1, "assets/ghosts/clyde.png"),
            new Ghost(1, 1, "assets/ghosts/inky.png"),
            new Ghost(1, 1, "assets/ghosts/pinky.png")
    };
    JPanel[] mapLabels = new JPanel[1000];

    public Game(LevelType id) {
        setLayout(new BorderLayout(0, 0));

        levelType = id;
        map = getLevel(id);
        add(createNewGamePanel());

        // draw and move loop
        new Thread(() -> {
            while (isRunning) {
                synchronized (lock) {
                    try {
                        handlePlayerMovement();
                        handleGhostsMovement();
                        handlePlayerGhostCollision();
                        handlePowerUpCollision();
                        SwingUtilities.invokeLater(() -> {
                            revalidate();
                            repaint();
                        });

                        Thread.sleep(250 / player.getSpeed());
                        lock.notify();
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // animations loop
        new Thread(() -> {
            String[] currentPlayerAnimation = playerAnimationRight;
            while (isRunning) {
                synchronized (lock) {
                    try {
                        for (int i = 0; i < 3; i++) {
                            currentPlayerAnimation = switch (player.getCurrentDirection()) {
                                case DOWN -> playerAnimationDown;
                                case LEFT -> playerAnimationLeft;
                                case RIGHT -> playerAnimationRight;
                                case UP -> playerAnimationUp;
                                default -> currentPlayerAnimation;
                            };

                            player.changeIcon(currentPlayerAnimation[i]);
                            lock.notify();
                            lock.wait();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // power up placement loop
        new Thread(() -> {
            while (isRunning) {
                try {
                    Thread.sleep(5000);
                    if ((int) (Math.random() * 4) == 0) {
                        for (Ghost ghost : ghosts) {
                            placePowerUp(ghost.getXpos(), ghost.getYpos());
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();

        // elapsed time loop
        new Thread(() -> {
            while (isRunning) {
                try {
                    Thread.sleep(1000);
                    elapsedSeconds++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // check for win loop
        new Thread(() -> {
            while (isRunning) {
                checkForWin();
            }
        }).start();

    }

    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    private void checkForWin() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length(); j++) {
                if (map[i].charAt(j) == '.') {
                    return;
                }
            }
        }
        isGameWon = true;
    }

    private void handleGhostsMovement() {
        for (Ghost ghost : ghosts) {
            switch (ghost.getDesiredDirection()) {
                case DOWN:
                    if (map[ghost.getYpos() + 1].charAt(ghost.getXpos()) != '#') {
                        ghost.setCurrentDirection(Direction.DOWN);
                        ghost.setDesiredDirection(Direction.DOWN);
                    }
                    break;
                case LEFT:
                    if (map[ghost.getYpos()].charAt(ghost.getXpos() - 1) != '#') {
                        ghost.setCurrentDirection(Direction.LEFT);
                        ghost.setDesiredDirection(Direction.LEFT);
                    }
                    break;
                case RIGHT:
                    if (map[ghost.getYpos()].charAt(ghost.getXpos() + 1) != '#') {
                        ghost.setCurrentDirection(Direction.RIGHT);
                        ghost.setDesiredDirection(Direction.RIGHT);
                    }
                    break;
                case UP:
                    if (map[ghost.getYpos() - 1].charAt(ghost.getXpos()) != '#') {
                        ghost.setCurrentDirection(Direction.UP);
                        ghost.setDesiredDirection(Direction.UP);
                    }
                    break;
            }

            int initialPosX = ghost.getXpos();
            int initialPosY = ghost.getYpos();


            int futurePosX = ghost.getXpos();
            int futurePosY = ghost.getYpos();

            switch (ghost.getCurrentDirection()) {
                case DOWN:
                    futurePosY = ghost.getYpos() + 1;
                    break;
                case LEFT:
                    futurePosX = ghost.getXpos() - 1;
                    break;
                case RIGHT:
                    futurePosX = ghost.getXpos() + 1;
                    break;
                case UP:
                    futurePosY = ghost.getYpos() - 1;
                    break;
            }

            if (map[futurePosY].charAt(futurePosX) == '#') {
                ghost.setDesiredDirection(Direction.getRandomDirection());
                continue;
            } else {
                switch (ghost.getDesiredDirection()) {
                    case DOWN:
                        if (map[ghost.getYpos() + 1].charAt(ghost.getXpos()) != '#') {
                            ghost.setCurrentDirection(Direction.DOWN);
                        }
                        break;
                    case LEFT:
                        if (map[ghost.getYpos()].charAt(ghost.getXpos() - 1) != '#') {
                            ghost.setCurrentDirection(Direction.LEFT);
                        }
                        break;
                    case RIGHT:
                        if (map[ghost.getYpos()].charAt(ghost.getXpos() + 1) != '#') {
                            ghost.setCurrentDirection(Direction.RIGHT);
                        }
                        break;
                    case UP:
                        if (map[ghost.getYpos() - 1].charAt(ghost.getXpos()) != '#') {
                            ghost.setCurrentDirection(Direction.UP);
                        }
                        break;
                }
            }

            switch (ghost.getCurrentDirection()) {
                case DOWN:
                    ghost.move(0, 1);
                    break;
                case LEFT:
                    ghost.move(-1, 0);
                    break;
                case RIGHT:
                    ghost.move(1, 0);
                    break;
                case UP:
                    ghost.move(0, -1);
                    break;
            }

            if (initialPosX != ghost.getXpos() || initialPosY != ghost.getYpos()) {
                if (map[initialPosY].charAt(initialPosX) == 'G') {
                    setBlank(mapLabels[initialPosY * map[0].length() + initialPosX]);
                    setGhost(mapLabels[ghost.getYpos() * map[0].length() + ghost.getXpos()], ghost);
                } else if (map[initialPosY].charAt(initialPosX) == '.') {
                    setDot(mapLabels[initialPosY * map[0].length() + initialPosX]);
                    setGhost(mapLabels[ghost.getYpos() * map[0].length() + ghost.getXpos()], ghost);
                } else if (map[initialPosY].charAt(initialPosX) == ' ') {
                    setBlank(mapLabels[initialPosY * map[0].length() + initialPosX]);
                    setGhost(mapLabels[ghost.getYpos() * map[0].length() + ghost.getXpos()], ghost);
                }
            }

            for (PowerUp p : powerUps) {
                if (map[initialPosY].charAt(initialPosX) == p.getSymbolOnMap()) {
                    setPowerUp(mapLabels[initialPosY * map[0].length() + initialPosX], p);
                }
            }


            if ((int) (Math.random() * 4) == 1) {
                ghost.setDesiredDirection(Direction.getRandomDirection());
            }
        }
    }

    private void handlePlayerGhostCollision() {
        if (!isAbleToEatGhosts) {
            for (Ghost ghost : ghosts) {
                if (player.getXpos() == ghost.getXpos() && player.getYpos() == ghost.getYpos()) {
                    player.decreaseHealth();
                    if (player.getHealth() == 0) {
                        isGameOver = true;
                        isRunning = false;
                    }
                    else
                        restartGame();
                }
            }
        }
    }

    private String[] getLevel(LevelType id) {
        String[] returnMap = new String[0];

        switch (id) {
            case LEVEL1:
                returnMap = new String[]{
                        "#########",
                        "#.......#",
                        "#.#.#.#.#",
                        "#.#.#.#.#",
                        "#...#.G.#",
                        "#.#.#.#.#",
                        "#.#.#...#",
                        "#..P..#.#",
                        "#########"
                };
                break;
            case LEVEL2:
                returnMap = new String[]{
                        "###########",
                        "#.........#",
                        "#.#.#.#.#.#",
                        "#.G.#.....#",
                        "#.#.#.#.#.#",
                        "#.........#",
                        "#.#.#.#.#.#",
                        "#.#.#.#...#",
                        "#.#.#.#.#.#",
                        "#..P......#",
                        "###########"
                };
                break;
            case LEVEL3:
                returnMap = new String[]{
                        "#############",
                        "#...........#",
                        "##.#.#.##.#.#",
                        "##.#.#......#",
                        "##.#.#.#.##.#",
                        "#...P..#.##.#",
                        "#.#.##.#.#..#",
                        "#.#......#.##",
                        "#.#.#.##.#.G#",
                        "#..........##",
                        "#############"
                };
                break;
            case LEVEL4:
                returnMap = new String[]{
                        "###############",
                        "#.............#",
                        "#.##.###.##.#.#",
                        "#...P.........#",
                        "#.##.###.##.#.#",
                        "#.##.###.##.#.#",
                        "#.##.###.##.#.#",
                        "#.............#",
                        "#.##.#.#.##.#.#",
                        "#.G#.....##.#.#",
                        "#.##.###.##.#.#",
                        "#........##...#",
                        "#.##.###.##.#.#",
                        "#.............#",
                        "###############"
                };
                break;
            case LEVEL5:
                returnMap = new String[]{
                        "#################",
                        "#...............#",
                        "#.##.##.##.##.#.#",
                        "#.##.##.##.##.#.#",
                        "#...P...........#",
                        "#.###.###.###.#.#",
                        "#.###.###.###.#.#",
                        "#...............#",
                        "#.#.#.#.#.###.#.#",
                        "#.#.#.#.#.###.#.#",
                        "#...............#",
                        "#.#.###.#######.#",
                        "#.#.#........#..#",
                        "#.#.#.######.#.##",
                        "#.#...######...G#",
                        "#...#........#.##",
                        "#################"
                };
                break;
            default:
                break;
        }
        return returnMap;
    }
    public JPanel createNewGamePanel() {
        JPanel gamePanel = new JPanel(new GridLayout(map.length, map[0].length(),0,0));

        gamePanel.setBackground(Color.BLACK);
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length(); j++) {
                JPanel panel = new JPanel();
                panel.setPreferredSize(new Dimension(50, 50));
                panel.setOpaque(true);

                switch (map[i].charAt(j)) {
                    case '#':
                        panel.setBackground(Color.BLUE);
                        mapLabels[i * map[0].length() + j] = panel;
                        break;
                    case '.':
                        JLabel dot = new JLabel();
                        dot.setBackground(Color.BLACK);
                        dot.setOpaque(true);
                        dot.setIcon(new ImageIcon(new ImageIcon("assets/other/dot.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
                        panel.add(dot);
                        panel.setBackground(Color.BLACK);
                        mapLabels[i * map[0].length() + j] = panel;

                        break;
                    case ' ':
                        panel.setBackground(Color.BLACK);
                        panel.setOpaque(false);
                        mapLabels[i * map[0].length() + j] = panel;
                        break;

                    case 'P':

                        if (!player.isSpawned()) {
                            player.spawn(j, i);
                        }

                        panel.setBackground(Color.BLACK);
                        panel.setOpaque(false);
                        mapLabels[i * map[0].length() + j] = panel;

                        break;
                    case 'G':
                        panel.setBackground(Color.RED);
                        mapLabels[i * map[0].length() + j] = panel;

                        for (Ghost ghost : ghosts) {
                            if (!ghost.isSpawned()) {
                                ghost.spawn(j, i);
                            }
                        }

                        break;
                    default:
                        break;
                }

                gamePanel.add(panel);
            }
        }
        return gamePanel;
    }

    public Player getPlayer() {
        return player;
    }

    private void handlePlayerMovement() {
        int initialPosX = player.getXpos();
        int initialPosY = player.getYpos();

        int futurePosX = player.getXpos();
        int futurePosY = player.getYpos();

        switch (player.getCurrentDirection()) {
            case DOWN:
                futurePosY = player.getYpos() + 1;
                break;
            case LEFT:
                futurePosX = player.getXpos() - 1;
                break;
            case RIGHT:
                futurePosX = player.getXpos() + 1;
                break;
            case UP:
                futurePosY = player.getYpos() - 1;
                break;
        }

        if (map[futurePosY].charAt(futurePosX) == '#' ||
                map[futurePosY].charAt(futurePosX) == 'G') {
            player.setCurrentDirection(Direction.STOP);
        } else {
            switch (player.getDesiredDirection()) {
                case DOWN:
                    if (map[player.getYpos() + 1].charAt(player.getXpos()) != '#' &&
                            map[player.getYpos() + 1].charAt(player.getXpos()) != 'G') {
                        player.setCurrentDirection(Direction.DOWN);

                        if (map[player.getYpos() + 1].charAt(player.getXpos()) == '.') {
                            map[player.getYpos() + 1] = map[player.getYpos() + 1].substring(0, player.getXpos()) + ' ' +
                                    map[player.getYpos() + 1].substring(player.getXpos() + 1);
                            player.increaseScore(10);

                        }
                    }
                    break;
                case LEFT:
                    if (map[player.getYpos()].charAt(player.getXpos() - 1) != '#' &&
                            map[player.getYpos()].charAt(player.getXpos() - 1) != 'G') {
                        player.setCurrentDirection(Direction.LEFT);

                        if (map[player.getYpos()].charAt(player.getXpos() - 1) == '.') {
                            map[player.getYpos()] = map[player.getYpos()].substring(0, player.getXpos() - 1) + ' ' + map[player.getYpos()].substring(player.getXpos());
                            player.increaseScore(10);
                        }
                    }
                    break;
                case RIGHT:
                    if (map[player.getYpos()].charAt(player.getXpos() + 1) != '#' &&
                            map[player.getYpos()].charAt(player.getXpos() + 1) != 'G') {
                        player.setCurrentDirection(Direction.RIGHT);

                        if (map[player.getYpos()].charAt(player.getXpos() + 1) == '.') {
                            map[player.getYpos()] = map[player.getYpos()].substring(0, player.getXpos() + 1) + ' ' + map[player.getYpos()].substring(player.getXpos() + 2);

                            player.increaseScore(10);
                        }
                    }
                    break;
                case UP:
                    if (map[player.getYpos() - 1].charAt(player.getXpos()) != '#' &&
                            map[player.getYpos() - 1].charAt(player.getXpos()) != 'G') {
                        player.setCurrentDirection(Direction.UP);

                        if (map[player.getYpos() - 1].charAt(player.getXpos()) == '.') {
                            map[player.getYpos() - 1] = map[player.getYpos() - 1].substring(0, player.getXpos()) + ' ' + map[player.getYpos() - 1].substring(player.getXpos() + 1);

                            player.increaseScore(10);
                        }
                    }
                    break;
            }
        }

        switch (player.getCurrentDirection()) {
            case DOWN:
                player.move(0, 1);
                break;
            case LEFT:
                player.move(-1, 0);
                break;
            case RIGHT:
                player.move(1, 0);
                break;
            case UP:
                player.move(0,-1);
                break;
        }
        if (initialPosX != player.getXpos() || initialPosY != player.getYpos()) {
            map[initialPosY] = map[initialPosY].substring(0, initialPosX) + " " + map[initialPosY].substring(initialPosX + 1);

            setBlank(mapLabels[initialPosY * map[0].length() + initialPosX]);
            setPacMan(mapLabels[player.getYpos() * map[0].length() + player.getXpos()]);
        }
    }

    public void setBlank(JPanel panel) {
        panel.setPreferredSize(new Dimension(50, 50));
        panel.setOpaque(true);
        panel.setBackground(Color.BLACK);
        panel.setVisible(false);
        panel.repaint();
    }
    public void setGhostSpawnCell(JPanel panel) {
        panel.setPreferredSize(new Dimension(50, 50));
        panel.setOpaque(true);
        panel.setBackground(Color.RED);
        panel.setVisible(true);
        panel.repaint();
    }

    public void setPacMan(JPanel panel) {
        panel.setVisible(true);
        panel.removeAll();

        panel.setPreferredSize(new Dimension(50, 50));
        panel.setOpaque(true);
        JLabel playerIcon = new JLabel();
        playerIcon.setIcon(new ImageIcon(player.getIcon().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        playerIcon.setBackground(Color.BLACK);
        playerIcon.setOpaque(true);

        playerIcon.repaint();

        panel.setOpaque(true);
        panel.setBackground(Color.BLACK);
        panel.add(playerIcon);
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int size = Math.min(panel.getWidth(), panel.getHeight());
                playerIcon.setIcon(new ImageIcon(player.getIcon().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
            }
        });
        panel.repaint();
    }

    public void setGhost(JPanel panel, Ghost ghost) {
        panel.setVisible(true);
        panel.removeAll();

        panel.setPreferredSize(new Dimension(50, 50));
        panel.setOpaque(true);
        JLabel ghostIcon = new JLabel();
        ghostIcon.setIcon(new ImageIcon(ghost.getIcon().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        ghostIcon.setBackground(Color.BLACK);
        ghostIcon.setOpaque(true);

        ghostIcon.repaint();

        panel.setOpaque(true);
        panel.setBackground(Color.BLACK);
        panel.add(ghostIcon);
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int size = Math.min(panel.getWidth(), panel.getHeight());
                ghostIcon.setIcon(new ImageIcon(ghost.getIcon().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
            }
        });
        panel.repaint();
    }

    public void setDot(JPanel panel) {
        panel.setVisible(true);
        panel.removeAll();

        panel.setPreferredSize(new Dimension(50, 50));
        panel.setOpaque(true);
        JLabel dotIcon = new JLabel();
        dotIcon.setIcon(new ImageIcon(new ImageIcon("assets/other/dot.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));

        dotIcon.setBackground(Color.BLACK);
        dotIcon.setOpaque(true);

        dotIcon.repaint();

        panel.setOpaque(true);
        panel.setBackground(Color.BLACK);
        panel.add(dotIcon);
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int size = Math.min(panel.getWidth(), panel.getHeight());
                dotIcon.setIcon(new ImageIcon(new ImageIcon("assets/other/dot.png").getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
            }
        });
        panel.repaint();
    }
    private void setPowerUp(JPanel panel, PowerUp powerUp) {
        panel.setVisible(true);
        panel.removeAll();

        panel.setPreferredSize(new Dimension(50, 50));
        panel.setOpaque(true);
        JLabel powerUpIcon = new JLabel();
        powerUpIcon.setIcon(new ImageIcon(new ImageIcon(powerUp.getIcon()).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));

        powerUpIcon.setBackground(Color.BLACK);
        powerUpIcon.setOpaque(true);

        powerUpIcon.repaint();

        panel.setOpaque(true);
        panel.setBackground(Color.BLACK);
        panel.add(powerUpIcon);
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int size = Math.min(panel.getWidth(), panel.getHeight());
                powerUpIcon.setIcon(new ImageIcon(new ImageIcon(powerUp.getIcon()).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
            }
        });
        panel.repaint();
    }
    private void restartGame() {

        player.setCurrentDirection(Direction.RIGHT);
        player.setDesiredDirection(Direction.RIGHT);
        player.setSpeed(1);
        player.setSpeedPowerUp(false);
        player.setScoreRate(1.0);
        map = getLevel(levelType);
        player.setScore(0);
        player.setIsSpawned(false);

        elapsedSeconds = 0;

        for (Ghost g : ghosts) {
            g.setIsSpawned(false);
        }

        removeAll();
        add(createNewGamePanel());
        revalidate();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length(); j++) {
                JPanel panel = mapLabels[i * map[0].length() + j];
                switch (map[i].charAt(j)) {
                    case '#':
                        panel.setBackground(Color.BLUE);
                        break;
                    case '.':
                        setDot(panel);
                        break;
                    case ' ':
                        panel.setBackground(Color.BLACK);
                        break;
                    case 'P':
                        setPacMan(panel);
                        break;
                    case 'G':
                        setGhostSpawnCell(panel);
                        break;
                    default:

                }
            }
        }
        repaint();
    }

    private void placePowerUp(int x, int y) {
        int random = (int) (Math.random() * 4);
        PowerUp powerUp = powerUps[random];

        map[y] = map[y].substring(0, x) + powerUp.getSymbolOnMap() + map[y].substring(x + 1);

    }
    private void handlePowerUpCollision() {
        for (PowerUp p : powerUps) {
            if (map[player.getYpos()].charAt(player.getXpos()) == p.getSymbolOnMap()) {
                switch (p.getSymbolOnMap()) {
                    case 's':
                        isSpeedPowerUp = true;
                        speedPowerUpExpireLoop();
                        break;
                    case 'r':
                        isDoubleScoreRate = true;
                        doubleScoreRateExpireLoop();
                        break;
                    case 'e':
                        isAbleToEatGhosts = true;
                        ableToEatGhostsPowerUp();
                        break;
                    case 'h':
                        player.incrementHealth();
                        break;
                    case 'd':
                        player.increaseScore(player.getScore());
                        break;
                }
                map[player.getYpos()] = map[player.getYpos()].substring(0, player.getXpos()) + ' ' + map[player.getYpos()].substring(player.getXpos() + 1);
            }
        }
    }

    private void speedPowerUpExpireLoop() {
        new Thread(() -> {
            try {
                if (isSpeedPowerUp) {

                    player.setSpeed(2);
                    Thread.sleep(3000);

                    player.setSpeed(1);
                    isSpeedPowerUp = false;
                }
            } catch (InterruptedException e) {
                    throw new RuntimeException(e);
            }
        }).start();
    }

    private void ableToEatGhostsPowerUp() {
        new Thread(() -> {
            try {
                if (isAbleToEatGhosts) {

                    for (Ghost ghost : ghosts) {
                        ghost.changeIcon("assets/ghosts/blue_ghost.png");
                    }

                    isAbleToEatGhostsLoop();

                    Thread.sleep(3000);

                    ghosts[0].changeIcon("assets/ghosts/blinky.png");
                    ghosts[1].changeIcon("assets/ghosts/clyde.png");
                    ghosts[2].changeIcon("assets/ghosts/inky.png");
                    ghosts[3].changeIcon("assets/ghosts/pinky.png");

                    isAbleToEatGhosts = false;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void isAbleToEatGhostsLoop() {
        new Thread(() -> {
            for (Ghost ghost : ghosts) {
                if (player.getXpos() == ghost.getXpos() && player.getYpos() == ghost.getYpos()) {
                    ghost.setIsSpawned(false);
                    for (int i = 0; i < map.length; i++) {
                        for (int j = 0; j < map[i].length(); j++) {
                            if (map[i].charAt(j) == 'G') {
                                ghost.spawn(j, i);
                                setGhostSpawnCell(mapLabels[i * map[0].length() + j]);
                            }
                        }
                    }
                    player.increaseScore(100);
                }
            }
        }).start();
    }

    private void doubleScoreRateExpireLoop() {
        new Thread(() -> {
            try {
                if (isDoubleScoreRate) {
                    player.setScoreRate(2.0);
                    Thread.sleep(3000);

                    isDoubleScoreRate = false;
                    player.setScoreRate(1.0);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    public boolean isRunning() {
        return isRunning;
    }

    public boolean isGameWon() {
        return isGameWon;
    }

    public void setGameWon(boolean gameWon) {
        isGameWon = gameWon;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

}
