import java.util.List;

public abstract class Consumer extends Animal {
    // Minimum age required to start breeding
    protected final int BREEDING_AGE;
    // Maximum age a consumer can have
    protected final int MAX_AGE;
    // Maximum number of births at any given step
    protected final int MAX_LITTER_SIZE;
    // Amount of food/energy
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
     * Determines whether the consumer can breed based on its age.
     *
     * @return true if the consumer can breed
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