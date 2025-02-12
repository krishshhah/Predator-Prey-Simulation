import java.util.List;
import java.util.Random;

public abstract class Consumer extends Animal {
    private static final Random rand = Randomizer.getRandom();

    protected final int BREEDING_AGE;
    protected final int MAX_AGE;
    protected final int MAX_LITTER_SIZE;
    protected int foodLevel;

    /**
     * Constructor for Consumer.
     */
    public Consumer(Location location, int breedingAge, int maxAge, int maxLitterSize, int foodLevel) {
        super(location);
        this.BREEDING_AGE = breedingAge;
        this.MAX_AGE = maxAge;
        this.MAX_LITTER_SIZE = maxLitterSize;
        this.foodLevel = foodLevel;

        lifeExpectancy = MAX_AGE;
    }

    /**
     * Increases the hunger level, potentially causing death.
     */
    protected void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Determines whether the animal can breed based on its age.
     */
    protected boolean canBreed() {
        return age >= BREEDING_AGE;
    }

    /**
     * Searches for food in the adjacent locations.
     * Overridden by subclasses.
     */
    protected abstract Location findFood(Field field);

    /**
     * Defines birth-giving behavior, implemented by subclasses.
     */
    protected abstract void giveBirth(Field nextFieldState, List<Location> freeLocations, List<Location> adjacentLocations);
}