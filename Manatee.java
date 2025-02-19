import java.util.List;
import java.util.Random;

/**
 * A simple model of a Manatee.
 * Manatee age, eat, move, breed, and die.
 *
 * @author David J. Barnes, Michael Kölling and Krish Shah
 * @version 7.4
 */

public class Manatee extends Prey {
    private static final Random rand = Randomizer.getRandom();
    // can be set to false if the user does not want to have a disease in the simulation.

    /**
     * Create a new Manatee. A Manatee may be created with age
     * zero (a newborn) or with a random age.
     *
     * @param randomAge If true, the Manatee will have a random age.
     * @param location  The location within the field.
     */
    public Manatee(boolean randomAge, Location location) {
        super(randomAge, location, 4, 25, 2, 10, 70);
    }

    /**
     * Defines the manatee's behaviour: aging, eating plants, breeding and moving.
     * Manatees don't move, breed, or feed during the night.
     *
     * @param currentField   The field occupied.
     * @param nextFieldState The updated field.
     * @param currentTime    The current time of the environment.
     */
    @Override
    public void act(Field currentField, Field nextFieldState, int currentTime, boolean isSunny) {
        incrementAge();
        incrementHunger();
        if (isAlive()) {
            if (!validTime(currentTime)) { // does not have any activity at night
                nextFieldState.placeAnimal(this, this.getLocation()); // stays in same location
                return; // nothing else happens - does not move, breed, eat/
            }
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            List<Location> adjacentLocations =
                    nextFieldState.getAdjacentLocations(getLocation());
            if (!freeLocations.isEmpty()) {
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
        return "Manatee{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Check whether this Manatee is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param nextFieldState    The updated field.
     * @param freeLocations     The locations that are free in the updated field.
     * @param adjacentLocations The adjacent locations.
     */
    @Override
    protected void giveBirth(Field nextFieldState, List<Location> freeLocations, List<Location> adjacentLocations) {
        // New Manatees are born into adjacent locations.
        // Get a list of adjacent free locations.
        int maleCount = 0;
        if (!this.isMale && canBreed()) { // if female - only females can 'give birth'
            // find the number of animals in the nextFieldState which are isMale manatees
            for (Location adjacentLocation : adjacentLocations) {
                if (nextFieldState.getAnimalAt(adjacentLocation) instanceof Manatee matingTurtle) {
                    if (matingTurtle.isMale) {
                        maleCount++;
                    }
                }
            }
            // gives as many births as possible into free adjacent locations
            // based on number of males in vicinity or max number of births
            for (int b = 0; b < maleCount && b < MAX_LITTER_SIZE && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Manatee young = new Manatee(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
}
