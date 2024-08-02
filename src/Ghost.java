public class Ghost extends Character {

    public Ghost(int xPos, int yPos, String iconPath) {
        super(xPos, yPos, iconPath);
        setCurrentDirection(Direction.LEFT);
        setDesiredDirection(Direction.LEFT);
    }
}
