import java.util.List;
import java.util.Random;

/**
 * A simple model of a Crocodile.
 * Crocodiles age, move, breed, and die.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Crocodile extends Prey {
    private static final Random rand = Randomizer.getRandom();
    public static boolean isCold = true;

    /**
     * Create a new Crocodile. A Crocodile may be created with age
     * zero (a newborn) or with a random age.
     *
     * @param randomAge If true, the Crocodile will have a random age.
     * @param location  The location within the field.
     */
    public Crocodile(boolean randomAge, Location location) {
        super(randomAge, location, 2, 45, 4, 7, 50);
    }

    public static String displayCold() {
        if (isCold) {
            return "Cold";
        } else {
            return "Hot";
        }
    }

    private static void changeTemperature(int currentTime) {
        isCold = !validTime(currentTime);
    }

    /**
     * This is what the Crocodile does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     *
     * @param currentField   The field occupied.
     * @param nextFieldState The updated field.
     * @param currentTime    The current time of the environment.
     */
    public void act(Field currentField, Field nextFieldState, int currentTime) {
        incrementAge();
        incrementHunger();
        changeTemperature(currentTime); // see if the temperature needs to change based on time
        if (isAlive()) {
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            List<Location> adjacentLocations =
                    nextFieldState.getAdjacentLocations(getLocation());
            if (isCold && !freeLocations.isEmpty()) {
                giveBirth(nextFieldState, freeLocations, adjacentLocations);
            }
            // Try to move into a free location.
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
        return "Crocodile{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Check whether this Crocodile is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param freeLocations The locations that are free in the current field.
     */
    protected void giveBirth(Field nextFieldState, List<Location> freeLocations, List<Location> adjacentLocations) {
        // New Crocodiles are born into adjacent locations.
        // Get a list of adjacent free locations.
        int maleCount = 0;
        if (!this.isMale && canBreed()) { // if female - only females can 'give birth'
            // find the number of animals in the nextFieldState which are isMale crocodiles
            for (Location adjacentLocation : adjacentLocations) {
                if (nextFieldState.getAnimalAt(adjacentLocation) instanceof Crocodile matingCrocodile) {
                    if (matingCrocodile.isMale) {
                        maleCount++;
                    }

                }
            }
            // gives as many births as possible into free adjacent locations
            // based on number of males in vicinity or max number of births
            for (int b = 0; b < maleCount && b < MAX_LITTER_SIZE && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Crocodile young = new Crocodile(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
}
