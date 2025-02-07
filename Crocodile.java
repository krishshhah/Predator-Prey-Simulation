import java.util.List;
import java.util.Random;

/**
 * A simple model of a Crocodile.
 * Crocodiles age, move, breed, and die.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Crocodile extends Animal
{
    // Characteristics shared by all Crocodiles (class variables).
    // The age at which a Crocodile can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a Crocodile can live.
    private static final int MAX_AGE = 45;
    // The likelihood of a Crocodile breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The Crocodile's age.
    private int age;
    // The Crocodile's gender, true if isMale, false if female

    /**
     * Create a new Crocodile. A Crocodile may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the Crocodile will have a random age.
     * @param location The location within the field.
     */
    public Crocodile(boolean randomAge, Location location)
    {
        super(location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        lifeExpectancy = MAX_AGE;
        isMale = rand.nextBoolean();
    }

    /**
     * This is what the Crocodile does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     * @param currentTime The current time of the environment.
     */
    public void act(Field currentField, Field nextFieldState, int currentTime)
    {
        incrementAge();

        if(isAlive()) {
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            List<Location> adjacentLocations =
                    nextFieldState.getAdjacentLocations(getLocation());
            if(validTime(currentTime) && !freeLocations.isEmpty()) {
                giveBirth(nextFieldState, freeLocations, adjacentLocations);
            }
            // Try to move into a free location.
            if(! freeLocations.isEmpty()) {
                Location nextLocation = freeLocations.get(0);
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
            }
            else {
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
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations, List<Location> adjacentLocations)
    {
        // New Crocodiles are born into adjacent locations.
        // Get a list of adjacent free locations.
        int maleCount = 0;
        if(! this.isMale && canBreed()) { // if female - only females can 'give birth'
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

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A Crocodile can breed if it has reached the breeding age.
     * @return true if the Crocodile can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
