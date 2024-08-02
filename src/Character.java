import javax.swing.*;
import java.awt.*;

public abstract class Character {
    private boolean isSpawned = false;
    private Direction currentDirection = Direction.RIGHT;
    private Direction desiredDirection = Direction.RIGHT;
    private int x, y;
    private Image icon;

    public Character(int xPos, int yPos, String iconPath) {
        this.x = xPos;
        this.y = yPos;
        this.icon = new ImageIcon(iconPath).getImage();
    }
    public int getXpos() {
        return x;
    }
    public int getYpos() {
        return y;
    }
    public Image getIcon() {
        return icon;
    }
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void setXpos(int x) {
        this.x = x;
    }
    public void setYpos(int y) {
        this.y = y;
    }
    public void changeIcon(String iconPath) {
        this.icon = new ImageIcon(iconPath).getImage();
    }
    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
    }
    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setIsSpawned(boolean spawned) {
        isSpawned = spawned;
    }

    public boolean isSpawned() {
        return isSpawned;
    }

    public Direction getDesiredDirection() {
        return desiredDirection;
    }

    public void setDesiredDirection(Direction desiredDirection) {
        this.desiredDirection = desiredDirection;
    }
    public void spawn(int posX, int posY) {
        setXpos(posX);
        setYpos(posY);
        setIsSpawned(true);
    }
}
