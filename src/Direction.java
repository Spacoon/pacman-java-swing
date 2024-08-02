public enum Direction {
    STOP, DOWN, LEFT, RIGHT, UP;
    public static Direction getRandomDirection() { // without STOP direction
        return values()[(int) (1 + Math.random() * (values().length - 1))];
    }
}
