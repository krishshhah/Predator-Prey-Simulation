import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class Predator extends Animal {
    protected final int BREEDING_AGE;
    protected final int MAX_AGE;
    protected final double BREEDING_PROBABILITY;
    protected final int MAX_LITTER_SIZE;
    protected final int TURTLE_FOOD_VALUE;
    protected final int CROCODILE_FOOD_VALUE;
    protected int foodLevel;
    private static final Random rand = Randomizer.getRandom();

    public Predator(Location location, int breedingAge, int maxAge, double breedingProbability, int maxLitterSize, int turtleFoodValue, int crocodileFoodValue, int foodLevel) {
        super(location);
        this.BREEDING_AGE = breedingAge;
        this.MAX_AGE = maxAge;
        this.BREEDING_PROBABILITY = breedingProbability;
        this.MAX_LITTER_SIZE = maxLitterSize;
        this.TURTLE_FOOD_VALUE = turtleFoodValue;
        this.CROCODILE_FOOD_VALUE = crocodileFoodValue;
        this.foodLevel = foodLevel;

        lifeExpectancy = MAX_AGE;
        isMale = rand.nextBoolean();
    }

    /**
     * This is what the Shark does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     *
     * @param currentField   The field currently occupied.
     * @param nextFieldState The updated field.
     * @param currentTime    The current time of the environment.
     */
    public void act(Field currentField, Field nextFieldState, int currentTime) {
        incrementAge();
        incrementHunger();
        if (isAlive()) {
            if (!validTime(currentTime)) { // does not have any activity at night
                nextFieldState.placeAnimal(this, this.getLocation()); // stays in same location
                return; // nothing else happens - does not move, breed, eat
            }
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            List<Location> adjacentLocationsLocations =
                    nextFieldState.getAdjacentLocations(getLocation());
            if (!freeLocations.isEmpty()) {
                giveBirth(nextFieldState, freeLocations, adjacentLocationsLocations);
            }
            // Move towards a source of food if found.
            Location nextLocation = findFood(currentField);
            if (nextLocation == null && !freeLocations.isEmpty()) {
                // No food found - try to move to a free location.
                nextLocation = freeLocations.remove(0);
            }
            // See if it was possible to move.
            if (nextLocation != null) {
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
            } else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Make this Shark more hungry. This could result in the Shark's death.
     */
    protected void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     *
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field) {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while (foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if (animal instanceof Turtle turtle) {
                if (turtle.isAlive()) {
                    turtle.setDead();
                    foodLevel = TURTLE_FOOD_VALUE;
                    foodLocation = loc;
                }
            } else if (animal instanceof Crocodile crocodile) {
                if (crocodile.isAlive()) {
                    crocodile.setDead();
                    foodLevel = CROCODILE_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }

    /**
     * Check whether this Shark is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param freeLocations The locations that are free in the current field.
     */
    protected abstract void giveBirth(Field nextFieldState, List<Location> freeLocations, List<Location> adjacentLocations);

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     *
     * @return The number of births (may be zero).
     */
    private int breed() {
        int births;
        if (canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        } else {
            births = 0;
        }
        return births;
    }

    /**
     * A Shark can breed if it has reached the breeding age.
     */
    protected boolean canBreed() {
        return age >= BREEDING_AGE;
    }
}
