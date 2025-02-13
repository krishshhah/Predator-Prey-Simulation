import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Defines all common variables and methods for prey,
 * who eat from plants and act in a similar way.
 *
 * @author David J. Barnes, Michael Kölling and Krish Shah
 * @version 7.1
 */

public abstract class Prey extends Consumer {
    private static final Random rand = Randomizer.getRandom();
    protected final int PLANT_BITE;

    public Prey(boolean randomAge, Location location, int breedingAge, int maxAge, int maxLitterSize, int plantBiteValue, int foodLevel) {
        super(location, breedingAge, maxAge, maxLitterSize, foodLevel);
        this.PLANT_BITE = plantBiteValue;

        if (randomAge) age = rand.nextInt(MAX_AGE);
    }

    /**
     * Defines prey behavior: movement, breeding, and grazing.
     */
    public abstract void act(Field currentField, Field nextFieldState, int currentTime);

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

    protected abstract void giveBirth(Field nextFieldState, List<Location> freeLocations, List<Location> adjacentLocations);
}