import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class Predator extends Consumer {
    private static final Random rand = Randomizer.getRandom();
    protected final int TURTLE_FOOD_VALUE;
    protected final int CROCODILE_FOOD_VALUE;

    public Predator(Location location, int breedingAge, int maxAge, double breedingProbability, int maxLitterSize, int turtleFoodValue, int crocodileFoodValue, int foodLevel) {
        super(location, breedingAge, maxAge, breedingProbability, maxLitterSize, foodLevel);
        this.TURTLE_FOOD_VALUE = turtleFoodValue;
        this.CROCODILE_FOOD_VALUE = crocodileFoodValue;
    }

    /**
     * Defines the predator's behavior: hunting, breeding, and moving.
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
     */
    protected Location findFood(Field field) {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while (foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if (animal instanceof Crocodile crocodile && crocodile.isAlive()) {
                crocodile.setDead();
                foodLevel = CROCODILE_FOOD_VALUE;
                foodLocation = loc;
            }else if (animal instanceof Turtle turtle && turtle.isAlive()) {
                turtle.setDead();
                foodLevel = TURTLE_FOOD_VALUE;
                foodLocation = loc;
            }
        }
        return foodLocation;
    }

    protected abstract void giveBirth(Field nextFieldState, List<Location> freeLocations, List<Location> adjacentLocations);
}