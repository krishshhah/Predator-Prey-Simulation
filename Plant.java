import java.util.List;
import java.util.Random;

public class Plant extends Animal {
    private static final int MAX_HEIGHT = 100;
    private static final Random rand = Randomizer.getRandom();
    private static final int GROWTH_RATE = 5;
    private int height; // height in meters

    public Plant(boolean randomHeight, Location location) {
        super(location);
        height = 0;
        if (randomHeight) {
            height = rand.nextInt(10, MAX_HEIGHT);
        }
    }

    public void act(Field currentField, Field nextFieldState, int currentTime) {
        incrementHeight();
        if (isAlive()) {
            nextFieldState.placeAnimal(this, this.getLocation());
            giveBirth(nextFieldState);
        } else {
            setDead();
        }
    }

    private void giveBirth(Field nextFieldState) {
        List<Location> freeLocations =
                nextFieldState.getFreeAdjacentLocations(getLocation());

        if (freeLocations.isEmpty()) return;

        if (breeding()) {
            Location location = freeLocations.remove(0);
            Plant young = new Plant(true, location);
            nextFieldState.placeAnimal(young, location);
        }
    }


    public void incrementHeight() {
        height += GROWTH_RATE;
        if (height > MAX_HEIGHT) {
            height = MAX_HEIGHT;
        }
    }

    public int eaten(int amount) {
        int foodGiven;
        if (amount >= height) {
            foodGiven = height;  // The turtle should only get what’s available
            height = 0;          // Plant is fully eaten
            setDead();           // Mark plant as dead
        } else {
            height -= amount;
            foodGiven = amount;
        }
        return foodGiven;
    }


    private boolean breeding() {
        return rand.nextDouble() < 0.01; // 1% chance of reproducing asexually
    }


    public int getHeight() {
        return height;
    }
}
