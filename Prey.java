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

}