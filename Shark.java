import java.util.List;
import java.util.Random;

/**
 * A simple model of a Shark.
 * Sharks age, move, eat rabbits, and die.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Shark extends Predator {
    // Characteristics shared by all Sharks (class variables).
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a Shark. A Shark can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Shark will have random age and hunger level.
     * @param location  The location within the field.
     */
    public Shark(boolean randomAge, Location location) {
        super(location, 2, 150, 5, 11, 14, rand.nextInt(11) + 10);
        if (randomAge) {
            age = rand.nextInt(lifeExpectancy);
        } else {
            age = 0;
        }
    }

    @Override
    public String toString() {
        return "Shark{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }

    /**
     * Check whether this Shark is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param nextFieldState    The updated field.
     * @param freeLocations     The locations that are free in the updated field.
     * @param adjacentLocations The adjacent locations.
     */
    @Override
    protected void giveBirth(Field nextFieldState, List<Location> freeLocations, List<Location> adjacentLocations) {
        // New Turtles are born into adjacent locations.
        // Get a list of adjacent free locations.
        int maleCount = 0;
        if (!this.isMale && canBreed()) { // if female - only females can 'give birth'
            // find the number of animals in the nextFieldState which are 'male sharks'
            for (Location adjacentLocation : adjacentLocations) {
                if (nextFieldState.getAnimalAt(adjacentLocation) instanceof Shark matingShark) {
                    if (matingShark.isMale) {
                        maleCount++;
                    }
                }
            }
            // gives as many births as possible into free adjacent locations
            // based on number of males in vicinity or max number of births possible
            for (int b = 0; b < maleCount && b < MAX_LITTER_SIZE && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Shark young = new Shark(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
}
