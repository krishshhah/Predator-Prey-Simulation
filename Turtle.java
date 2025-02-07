import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a Turtle.
 * Turtles age, move, breed, and die.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Turtle extends Animal {
    // Characteristics shared by all Turtles (class variables).
    // The age at which a Turtle can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a Turtle can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a Turtle breeding.
    private static final double BREEDING_PROBABILITY = 0.12;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // true = disease likely to exist, false = will not
    private final static boolean diseasePop = true;
    private static final int PLANT_BITE = 5;
    private boolean hasDisease;
    private int foodLevel;

    /**
     * Create a new Turtle. A Turtle may be created with age
     * zero (a new born) or with a random age.
     *
     * @param randomAge If true, the Turtle will have a random age.
     * @param location  The location within the field.
     */
    public Turtle(boolean randomAge, Location location) {
        super(location);
        if (randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        isMale = rand.nextBoolean();
        double diseaseChance = rand.nextDouble();
        hasDisease = diseaseChance < 0.1; // x% of having a disease
        // if they have a disease, they only get 5 steps after catching disease (lives)
        lifeExpectancy = (diseasePop && hasDisease) ? age + 5 : MAX_AGE;
        foodLevel = 50;
    }

    /**
     * This is what the Turtle does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     *
     * @param currentField   The field occupied.
     * @param nextFieldState The updated field.
     * @param currentTime    The current time of the environment.
     */
    public void act(Field currentField, Field nextFieldState, int currentTime) {
        incrementAge();
        incrementHunger();
        if (isAlive()) {
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            List<Location> adjacentLocations =
                    nextFieldState.getAdjacentLocations(getLocation());
            if (validTime(currentTime) && !freeLocations.isEmpty()) {
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


    /**
     * if the animal catches disease then can only move 5 steps more than
     */
    public void catchDisease() {
        if (diseasePop && !hasDisease) {
            hasDisease = true;
            lifeExpectancy = age + 5;
        }
    }

    @Override
    public String toString() {
        return "Turtle{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Check whether this Turtle is to give birth at this step.
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
                if (nextFieldState.getAnimalAt(adjacentLocation) instanceof Turtle matingTurtle) {
                    if (matingTurtle.isMale) {
                        // if they mate: the isMale gets a disease if female has it
                        if ((this.hasDisease || matingTurtle.hasDisease) && rand.nextDouble() < 0.5) { // 50% chance of transmission
                            this.catchDisease();
                            matingTurtle.catchDisease();
                        }
                        maleCount++;
                    }
                }
            }
            // gives as many births as possible into free adjacent locations
            // based on number of males in vicinity or max number of births
            for (int b = 0; b < maleCount && b < MAX_LITTER_SIZE && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Turtle young = new Turtle(false, loc);
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
     * A Turtle can breed if it has reached the breeding age.
     *
     * @return true if the Turtle can breed, false otherwise.
     */
    private boolean canBreed() {
        return age >= BREEDING_AGE;
    }

    private Location findFood(Field field) {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while (foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if (animal instanceof Plant plant) {
                if (plant.isAlive()) {
                    foodLevel = plant.eaten(PLANT_BITE);
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }

    private void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }
}
