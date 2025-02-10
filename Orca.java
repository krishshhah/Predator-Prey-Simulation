import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a Orca.
 * Sharks age, move, eat rabbits, and die.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Orca extends Animal {
    // Characteristics shared by all Sharks (class variables).
    // The age at which a Orca can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a Orca can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a Orca breeding.
    private static final double BREEDING_PROBABILITY = 0.2;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a Orca can go before it has to eat again.
    private static final int TURTLE_FOOD_VALUE = 8;

    private static final int CROCODILE_FOOD_VALUE = 10;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // The Orca's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create a Orca. A Orca can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Orca will have random age and hunger level.
     * @param location  The location within the field.
     */
    public Orca(boolean randomAge, Location location) {
        super(location);
        if (randomAge) {
            age = rand.nextInt(MAX_AGE);
        } else {
            age = 0;
        }
        lifeExpectancy = MAX_AGE;
        foodLevel = rand.nextInt(TURTLE_FOOD_VALUE) + 30;
        isMale = rand.nextBoolean();
    }

    /**
     * This is what the Orca does most of the time: it hunts for
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
            if (!validTime(currentTime)) { //weather colder during the night
                nextFieldState.placeAnimal(this, this.getLocation()); // stays in same location
                return; // nothing else happens - does not move, breed, eat/
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


    @Override
    public String toString() {
        return "Orca{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }


    /**
     * Make this Orca more hungry. This could result in the Orca's death.
     */
    private void incrementHunger() {
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
     * Check whether this Orca is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations, List<Location> adjacentLocations) {
        // New Turtles are born into adjacent locations.
        // Get a list of adjacent free locations.
        int maleCount = 0;
        if (!this.isMale && canBreed()) { // if female - only females can 'give birth'
            // find the number of animals in the nextFieldState which are isMale turtles
            for (Location adjacentLocation : adjacentLocations) {
                if (nextFieldState.getAnimalAt(adjacentLocation) instanceof Orca matingOrca) {
                    if (matingOrca.isMale) {
                        maleCount++;
                    }
                }
            }
            // gives as many births as possible into free adjacent locations
            // based on number of males in vicinity or max number of births
            for (int b = 0; b < maleCount && b < MAX_LITTER_SIZE && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Orca young = new Orca(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }

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
     * A Orca can breed if it has reached the breeding age.
     */
    private boolean canBreed() {
        return age >= BREEDING_AGE;
    }
}
