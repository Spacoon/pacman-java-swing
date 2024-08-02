import javax.swing.*;
import java.awt.*;

public class SpeedPowerUp implements PowerUp {
    @Override
    public char getSymbolOnMap() {
        return 's';
    }
    @Override
    public Image getIcon() {
        return new ImageIcon("assets/other/strawberry2.png").getImage();
    }
}