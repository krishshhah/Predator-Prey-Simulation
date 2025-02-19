import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Defines all common variables and methods for prey,
 * who eat from plants and act in a similar way.
 *
 * @author Krish Shah
 * @version 1.0
 */

public abstract class Prey extends Consumer {
    private static final Random rand = Randomizer.getRandom();
    // The amount of energy a prey receives from biting a plant.
    protected final int PLANT_BITE;

    public Prey(boolean randomAge, Location location, int breedingAge, int maxAge, int maxLitterSize, int plantBiteValue, int foodLevel) {
        super(location, breedingAge, maxAge, maxLitterSize, foodLevel);
        this.PLANT_BITE = plantBiteValue;

        if (randomAge) age = rand.nextInt(MAX_AGE);
    }

    /**
     * Searches for plants to eat in adjacent locations.
     */
    protected Location findFood(Field field) {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while (foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if (animal instanceof Plant plant && plant.isAlive()) {
                foodLevel = plant.eaten(PLANT_BITE);
                foodLocation = loc;
            }
        }
        return foodLocation;
    }

    /**
     * Defines the prey's behaviour: aging, eating plants, breeding and moving.
     * Prey don't move, breed, or feed during the night.
     * This method is for general prey, can be overwritten in subclasses for special implementations.
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

}