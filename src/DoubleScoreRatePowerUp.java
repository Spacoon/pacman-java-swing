import javax.swing.*;
import java.awt.*;

public class DoubleScoreRatePowerUp implements PowerUp {
    @Override
    public char getSymbolOnMap() {
        return 'r';
    }

    @Override
    public Image getIcon() {
        return new ImageIcon("assets/other/apple2.png").getImage();
    }
}
