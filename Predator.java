import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Defines all common variables and methods for predators,
 * who hunt, act, and eat in similar way.
 *
 * @author David J. Barnes, Michael Kölling and Krish Shah
 * @version 7.1
 */

public abstract class Predator extends Consumer {
    private static final Random rand = Randomizer.getRandom();
    // The energy gained by eating a turtle
    protected final int TURTLE_FOOD_VALUE;
    // The energy gained by eating a crocodile
    protected final int IGUANA_FOOD_VALUE;

    public Predator(Location location, int breedingAge, int maxAge, int maxLitterSize, int turtleFoodValue, int iguanaFoodValue, int foodLevel) {
        super(location, breedingAge, maxAge, maxLitterSize, foodLevel);
        this.TURTLE_FOOD_VALUE = turtleFoodValue;
        this.IGUANA_FOOD_VALUE = iguanaFoodValue;
    }

    /**
     * Defines the predator's behavior: aging, hunting, breeding, and moving.
     *
     * @param currentField   The field occupied.
     * @param nextFieldState The updated field.
     * @param currentTime    The current time of the environment.
     */
    public void act(Field currentField, Field nextFieldState, int currentTime) {
        incrementAge();
        incrementHunger();
        if (isAlive()) {
            if (!validTime(currentTime)) {
                nextFieldState.placeAnimal(this, this.getLocation());
                return;
            }
            List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());
            List<Location> adjacentLocations = nextFieldState.getAdjacentLocations(getLocation());
            Location nextLocation = findFood(currentField);
            if (!freeLocations.isEmpty()) {
                giveBirth(nextFieldState, freeLocations, adjacentLocations);
            }
            if (nextLocation == null && !freeLocations.isEmpty()) {
                nextLocation = freeLocations.remove(0);
            }
            if (nextLocation != null) {
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
            } else {
                setDead();
            }
        }
    }

    /**
     * Searches for prey in adjacent locations.
     *
     * @param field The current field occupied.
     * @return the location that the predator can move into where the prey previously was
     * null if it does not eat.
     */
    protected Location findFood(Field field) {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator(); // looks through adjacent locations
        Location foodLocation = null;
        while (foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if (animal instanceof Iguana iguana && iguana.isAlive()) {
                iguana.setDead();
                foodLevel = IGUANA_FOOD_VALUE;
                foodLocation = loc;
            } else if (animal instanceof Turtle turtle && turtle.isAlive()) {
                turtle.setDead();
                foodLevel = TURTLE_FOOD_VALUE;
                foodLocation = loc;
            }
        }
        return foodLocation;
    }

    /**
     * Allows predators to give birth, override by subclasses.
     */
    protected abstract void giveBirth(Field nextFieldState, List<Location> freeLocations, List<Location> adjacentLocations);
}