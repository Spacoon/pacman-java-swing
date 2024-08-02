import javax.swing.*;

public class LevelSelector {
    LevelLabel[] levels = {
            new LevelLabel("Level 1", LevelType.LEVEL1),
            new LevelLabel("Level 2", LevelType.LEVEL2),
            new LevelLabel("Level 3", LevelType.LEVEL3),
            new LevelLabel("Level 4", LevelType.LEVEL4),
            new LevelLabel("Level 5", LevelType.LEVEL5)
    };
    private boolean cancelled = false;

    public LevelSelector() {
        LevelLabel level = (LevelLabel) JOptionPane.showInputDialog(
                null,
                "Choose a level",
                "Level Selector",
                JOptionPane.QUESTION_MESSAGE, null, levels, levels[0]
        );

        if (level == null) {
            cancelled = true;
            System.out.println("You cancelled");
        } else {
            new GameAndHud(level.getId());
        }
    }

    public boolean isCancelled() {
        return cancelled;
    }
}

class LevelLabel extends JLabel {
    private final LevelType id;

    public LevelLabel(String text, LevelType id) {
        super(text);
        this.id = id;
    }
    public LevelType getId() {
        return id;
    }

    @Override
    public String toString() {
        return getText();
    }
}
