import java.util.Iterator;
import java.util.List;

/**
 * Defines all common variables and methods for predators,
 * who hunt, act, and eat in similar way.
 *
 * @author Krish Shah
 * @version 1.0
 */

public abstract class Predator extends Consumer {
    // The energy gained by eating a turtle
    protected final int TURTLE_FOOD_VALUE;
    // The energy gained by eating an iguana
    protected final int IGUANA_FOOD_VALUE;
    // The energy gained by eating a manatee
    protected final int MANATEE_FOOD_VALUE;

    public Predator(Location location, int breedingAge, int maxAge, int maxLitterSize, int turtleFoodValue, int iguanaFoodValue, int manateeFoodValue, int foodLevel) {
        super(location, breedingAge, maxAge, maxLitterSize, foodLevel);
        this.TURTLE_FOOD_VALUE = turtleFoodValue;
        this.IGUANA_FOOD_VALUE = iguanaFoodValue;
        this.MANATEE_FOOD_VALUE = manateeFoodValue;

    }

    /**
     * Defines the predator's behavior: aging, hunting, breeding, and moving.
     * Predators do not move, breed or hunt during the night.
     *
     * @param currentField   The field occupied.
     * @param nextFieldState The updated field.
     * @param currentTime    The current time of the environment.
     */
    public void act(Field currentField, Field nextFieldState, int currentTime, boolean isSunny) {
        incrementAge();
        incrementHunger();
        if (isAlive()) {
            if (!validTime(currentTime) || !isSunny) { // at night or if its cloudy/rainy
                nextFieldState.placeAnimal(this, this.getLocation()); // stay in the same location.
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
            } else if (animal instanceof Manatee manatee && manatee.isAlive()) {
                manatee.setDead();
                foodLevel = MANATEE_FOOD_VALUE;
                foodLocation = loc;
            } else if (animal instanceof Turtle turtle && turtle.isAlive()) {
                turtle.setDead();
                foodLevel = TURTLE_FOOD_VALUE;
                foodLocation = loc;
            }
        }
        return foodLocation;
    }
}