import javax.swing.*;
import java.awt.*;

public class EatGhostsPowerUp implements PowerUp {
    @Override
    public char getSymbolOnMap() {
        return 'e';
    }

    @Override
    public Image getIcon() {
        return new ImageIcon("assets/other/apple3.png").getImage();
    }
}
