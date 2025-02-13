import java.util.List;
import java.util.Random;

public class Plant extends Animal {
    // Maximum height a plant can grow to.
    private static final int MAX_HEIGHT = 500;
    // For random behaviour.
    private static final Random rand = Randomizer.getRandom();
    // Rate at which plant grows with each step.
    private static final int GROWTH_RATE = 5;
    // Height in meters.
    private int height;

    public Plant(boolean randomHeight, Location location) {
        super(location);
        height = 0;
        if (randomHeight) {
            height = rand.nextInt(MAX_HEIGHT); // populates with random heights
        }
    }

    /**
     * Defines the plant's behaviour: growing, breeding, and not moving
     *
     * @param currentField   The field occupied.
     * @param nextFieldState The updated field.
     * @param currentTime    The current time of the environment.
     */
    public void act(Field currentField, Field nextFieldState, int currentTime) {
        incrementHeight(); // height is increased
        if (isAlive()) {
            nextFieldState.placeAnimal(this, this.getLocation());
            giveBirth(nextFieldState, currentTime);
        } else {
            setDead();
        }
    }

    private void giveBirth(Field nextFieldState, int currentTime) {
        List<Location> freeLocations =
                nextFieldState.getFreeAdjacentLocations(getLocation());

        if (freeLocations.isEmpty()) return;

        if (canBreed(currentTime)) {
            Location location = freeLocations.remove(0);
            Plant young = new Plant(true, location);
            nextFieldState.placeAnimal(young, location);
        }
    }


    public void incrementHeight() {
        height += GROWTH_RATE;
        height = Math.min(height, MAX_HEIGHT);
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


    private boolean canBreed(int currentTime) {
        return validTime(currentTime) && rand.nextDouble() < 0.01; // 1% chance of reproducing asexually
    }

}
