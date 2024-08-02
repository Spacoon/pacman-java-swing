import javax.swing.*;
import java.awt.*;

public class IncrementHealthPowerUp implements PowerUp {
    @Override
    public char getSymbolOnMap() {
        return 'h';
    }

    @Override
    public Image getIcon() {
        return new ImageIcon("assets/other/apple.png").getImage();
    }
}
