import java.util.List;

/**
 * A simple model of an Iguana.
 * Iguanas age, eat, move, breed, and die.
 *
 * @author Krish Shah
 * @version 1.4
 */
public class Iguana extends Prey {
    /**
     * Create a new Iguana. An Iguana may be created with age
     * zero (a newborn) or with a random age.
     *
     * @param randomAge If true, the Iguana will have a random age.
     * @param location  The location within the field.
     */
    public Iguana(boolean randomAge, Location location) {
        super(randomAge, location, 2, 60, 4, 9, 50);
    }

    /**
     * Helps display the current weather, used by the SimulatorView Class
     *
     * @return String displaying "Hot" or "Cold" weather based on the current time of the environment.
     */
    public static String displayCold(int currentTime) {
        if (validTime(currentTime)) {
            return "Hot";
        } else {
            return "Cold";
        }
    }

    /**
     * Defines the iguana's behaviour: aging, eating plants, breeding and moving.
     * The iguana can still move and eat at any time but does not breed during the day due to warmer waters.
     *
     * @param currentField   The field occupied.
     * @param nextFieldState The updated field.
     * @param currentTime    The current time of the environment.
     */
    public void act(Field currentField, Field nextFieldState, int currentTime, boolean isSunny) {
        incrementAge();
        incrementHunger();
        if (isAlive()) {
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            List<Location> adjacentLocations =
                    nextFieldState.getAdjacentLocations(getLocation());
            if (!validTime(currentTime) && !freeLocations.isEmpty()) { // can only breed in cold waters
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
        return "Iguana{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Check whether this Iguana is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param nextFieldState    The updated field.
     * @param freeLocations     The locations that are free in the updated field.
     * @param adjacentLocations The adjacent locations.
     */
    protected void giveBirth(Field nextFieldState, List<Location> freeLocations, List<Location> adjacentLocations) {
        // New Iguanas are born into adjacent locations.
        // Get a list of adjacent free locations.
        int maleCount = 0;
        if (!this.isMale && canBreed()) { // if female - only females can 'give birth'
            // find the number of animals in the nextFieldState which are isMale iguanas
            for (Location adjacentLocation : adjacentLocations) {
                if (nextFieldState.getAnimalAt(adjacentLocation) instanceof Iguana matingIguana) {
                    if (matingIguana.isMale) {
                        maleCount++;
                    }

                }
            }
            // gives as many births as possible into free adjacent locations
            // based on number of males in vicinity or max number of births
            for (int b = 0; b < maleCount && b < MAX_LITTER_SIZE && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Iguana young = new Iguana(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
}
