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
    // The age at which a Shark can start to breed.
//    private static final int BREEDING_AGE = 3;
//    // The age to which a Shark can live.
//    private static final int MAX_AGE = 150;
//    // The likelihood of a Shark breeding.
//    private static final double BREEDING_PROBABILITY = 0.20;
//    // The maximum number of births.
//    private static final int MAX_LITTER_SIZE = 3;
//    // The food value of a single rabbit. In effect, this is the
//    // number of steps a Shark can go before it has to eat again.
//    private static final int TURTLE_FOOD_VALUE = 11;
//
//    private static final int CROCODILE_FOOD_VALUE = 14;
//    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();


    // Individual characteristics (instance fields).

    // The Shark's age.
    // The Shark's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create a Shark. A Shark can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Shark will have random age and hunger level.
     * @param location  The location within the field.
     */
    public Shark(boolean randomAge, Location location) {
        super(location, 2, 150, 0.25, 5, 11, 14, rand.nextInt(11) + 10);
        if (randomAge) {
            age = rand.nextInt(MAX_AGE);
        } else {
            age = 0;
        }
    }

//    /**
//     * This is what the Shark does most of the time: it hunts for
//     * rabbits. In the process, it might breed, die of hunger,
//     * or die of old age.
//     *
//     * @param currentField   The field currently occupied.
//     * @param nextFieldState The updated field.
//     * @param currentTime    The current time of the environment.
//     */
//    public void act(Field currentField, Field nextFieldState, int currentTime) {
//        incrementAge();
//        incrementHunger();
//        if (isAlive()) {
//            if (!validTime(currentTime)) { //weather colder during the night
//                nextFieldState.placeAnimal(this, this.getLocation()); // stays in same location
//                return; // nothing else happens - does not move, breed, eat/
//            }
//            List<Location> freeLocations =
//                    nextFieldState.getFreeAdjacentLocations(getLocation());
//            List<Location> adjacentLocationsLocations =
//                    nextFieldState.getAdjacentLocations(getLocation());
//            if (!freeLocations.isEmpty()) {
//                giveBirth(nextFieldState, freeLocations, adjacentLocationsLocations);
//            }
//            // Move towards a source of food if found.
//            Location nextLocation = findFood(currentField);
//            if (nextLocation == null && !freeLocations.isEmpty()) {
//                // No food found - try to move to a free location.
//                nextLocation = freeLocations.remove(0);
//            }
//            // See if it was possible to move.
//            if (nextLocation != null) {
//                setLocation(nextLocation);
//                nextFieldState.placeAnimal(this, nextLocation);
//            } else {
//                // Overcrowding.
//                setDead();
//            }
//        }
//    }


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
     * Make this Shark more hungry. This could result in the Shark's death.
     */
//    private void incrementHunger() {
//        foodLevel--;
//        if (foodLevel <= 0) {
//            setDead();
//        }
//    }

//    /**
//     * Look for rabbits adjacent to the current location.
//     * Only the first live rabbit is eaten.
//     *
//     * @param field The field currently occupied.
//     * @return Where food was found, or null if it wasn't.
//     */
//    private Location findFood(Field field) {
//        List<Location> adjacent = field.getAdjacentLocations(getLocation());
//        Iterator<Location> it = adjacent.iterator();
//        Location foodLocation = null;
//        while (foodLocation == null && it.hasNext()) {
//            Location loc = it.next();
//            Animal animal = field.getAnimalAt(loc);
//            if (animal instanceof Turtle turtle) {
//                if (turtle.isAlive()) {
//                    turtle.setDead();
//                    foodLevel = TURTLE_FOOD_VALUE;
//                    foodLocation = loc;
//                }
//            } else if (animal instanceof Crocodile crocodile) {
//                if (crocodile.isAlive()) {
//                    crocodile.setDead();
//                    foodLevel = CROCODILE_FOOD_VALUE;
//                    foodLocation = loc;
//                }
//            }
//        }
//        return foodLocation;
//    }

    /**
     * Check whether this Shark is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param freeLocations The locations that are free in the current field.
     */
    @Override
    protected void giveBirth(Field nextFieldState, List<Location> freeLocations, List<Location> adjacentLocations) {
        // New Turtles are born into adjacent locations.
        // Get a list of adjacent free locations.
        int maleCount = 0;
        if (!this.isMale && canBreed()) { // if female - only females can 'give birth'
            // find the number of animals in the nextFieldState which are isMale turtles
            for (Location adjacentLocation : adjacentLocations) {
                if (nextFieldState.getAnimalAt(adjacentLocation) instanceof Shark matingShark) {
                    if (matingShark.isMale) {
                        maleCount++;
                    }
                }
            }
            // gives as many births as possible into free adjacent locations
            // based on number of males in vicinity or max number of births
            for (int b = 0; b < maleCount && b < MAX_LITTER_SIZE && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Shark young = new Shark(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }

//    /**
//     * Generate a number representing the number of births,
//     * if it can breed.
//     *
//     * @return The number of births (may be zero).
//     */
//    private int breed() {
//        int births;
//        if (canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
//            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
//        } else {
//            births = 0;
//        }
//        return births;
//    }
//
//    /**
//     * A Shark can breed if it has reached the breeding age.
//     */
//    private boolean canBreed() {
//        return age >= BREEDING_AGE;
//    }
}
