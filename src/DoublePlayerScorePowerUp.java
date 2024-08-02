import javax.swing.*;
import java.awt.*;

public class DoublePlayerScorePowerUp implements PowerUp {
    @Override
    public char getSymbolOnMap() {
        return 'd';
    }

    @Override
    public Image getIcon() {
        return new ImageIcon("assets/other/strawberry.png").getImage();
    }
}