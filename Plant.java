import java.util.List;
import java.util.Random;

/**
 * Defines all common variables and methods for plants who grow,
 * which consumers, specifically prey, feed off of.
 *
 * @author Krish Shah
 * @version 1.3
 */
public class Plant extends Animal {
    // Maximum height a plant can grow to.
    private static final int MAX_HEIGHT = 500;
    // For random behaviour.
    private static final Random rand = Randomizer.getRandom();
    // Rate at which plant grows with each step.
    private static final int GROWTH_RATE = 5;
    // Height in meters.
    private int height;

    /**
     * Constructor for the Producers of the ecosystem.
     */
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
    public void act(Field currentField, Field nextFieldState, int currentTime, boolean isSunny) {
        incrementHeight(); // height is increased
        if (isAlive()) {
            nextFieldState.placeAnimal(this, this.getLocation());
            giveBirth(nextFieldState, currentTime, isSunny);
        } else {
            setDead();
        }
    }

    /**
     * Defines the plant's behaviour: growing, breeding, and not moving
     *
     * @param nextFieldState The updated field.
     * @param currentTime    The current time of the environment.
     */
    private void giveBirth(Field nextFieldState, int currentTime, boolean isSunny) {
        List<Location> freeLocations =
                nextFieldState.getFreeAdjacentLocations(getLocation());

        if (freeLocations.isEmpty()) return; // nowhere to move

        if (canBreed(currentTime, isSunny)) {
            Location location = freeLocations.remove(0);
            Plant young = new Plant(true, location);
            // new plant is placed in a free location
            nextFieldState.placeAnimal(young, location);
        }
    }

    /**
     * Allows the plant to grow with each step using a predefined growth rate.
     */
    private void incrementHeight() {
        height += GROWTH_RATE;
        // cannot grow past the maximum height
        height = Math.min(height, MAX_HEIGHT);
    }

    /**
     * Enables a plant to be eaten; they die if they are eaten entirely.
     *
     * @param amount The amount of 'energy' a prey desires to eat off a plant.
     * @return The actual amount of 'energy' a prey receives after
     */
    public int eaten(int amount) {
        int foodGiven;
        if (amount >= height) {
            foodGiven = height;  // The prey should only get what’s available
            height = 0;          // Plant is fully eaten
            setDead();           // Mark plant as dead
        } else {
            height -= amount;
            foodGiven = amount;
        }
        return foodGiven;
    }

    /**
     * True if the plant can breed asexually.
     *
     * @param currentTime The current time of the environment to check if plant is 'awake'.
     * @param isSunny     The plant can only breed with sunlight energy
     * @return True if the plant beats the odds and is 'awake'.
     */
    private boolean canBreed(int currentTime, boolean isSunny) {
        return validTime(currentTime) && rand.nextDouble() < 0.01 && isSunny;
        // 1% chance of reproducing asexually
    }

}
