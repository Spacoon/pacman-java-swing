public class Player extends Character {
    private int speed = 1;
    private boolean isAbleToEatGhosts = false;
    private boolean isSpeedPowerUp = false;
    private boolean isDoubleScoreRatePowerUp = false;
    private int score = 0;
    private double scoreRate = 1.0;
    private int health = 3;
    public Player(int xPos, int yPos, String iconPath) {
        super(xPos, yPos, iconPath);
    }
    public void decreaseHealth() {
        health--;
    }
    public void incrementHealth() {
        health++;
    }
    public int getHealth() {
        return health;
    }

    public int getScore() {
        return score;
    }
    public void increaseScore(int score) {
        this.score += (int) (scoreRate * score);
    }

    public void setScore(int score) {
        this.score = (int) (score * scoreRate);
    }

    public void setScoreRate(double scoreRate) {
        this.scoreRate = scoreRate;
    }
    public void setSpeedPowerUp(boolean speedPowerUp) {
        isSpeedPowerUp = speedPowerUp;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public int getSpeed() {
        return speed;
    }
}
