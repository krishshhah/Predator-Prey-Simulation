import java.util.List;
import java.util.Random;

/**
 * A simple model of an Orca.
 * Orcas age, move, hunt, breed, and die.
 *
 * @author Krish Shah
 * @version 1.4
 */
public class Orca extends Predator {
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Creates an Orca. An Orca can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Orca will have random age and hunger level.
     * @param location  The location within the field.
     */
    public Orca(boolean randomAge, Location location) {
        super(location, 2, 150, 5, 11, 14, rand.nextInt(11) + 10);
        if (randomAge) {
            age = rand.nextInt(MAX_AGE);
        } else {
            age = 0;
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
     * Check whether this Orca is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param freeLocations     The adjacent locations that are free in the current field.
     * @param adjacentLocations The adjacent locations.
     */
    @Override
    protected void giveBirth(Field nextFieldState, List<Location> freeLocations, List<Location> adjacentLocations) {
        // New Turtles are born into adjacent locations.
        // Get a list of adjacent free locations.
        int maleCount = 0;
        if (!this.isMale && canBreed()) { // if female - only females can 'give birth'
            // find the number of animals in the nextFieldState which are 'male crocodiles'
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

}
