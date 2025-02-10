import java.util.List;
import java.util.Random;

public abstract class Herbivore extends Animal {
    protected static final Random rand = Randomizer.getRandom();

    protected final int BREEDING_AGE;
    protected final int MAX_AGE;
    protected final double BREEDING_PROBABILITY;
    protected final int MAX_LITTER_SIZE;
    protected final int PLANT_BITE;
    protected int foodLevel;

    public Herbivore(Location location, int breedingAge, int maxAge, double breedingProbability, int maxLitterSize, int plantBite, int foodLevel) {
        super(location);
        this.BREEDING_AGE = breedingAge;
        this.MAX_AGE = maxAge;
        this.BREEDING_PROBABILITY = breedingProbability;
        this.MAX_LITTER_SIZE = maxLitterSize;
        this.PLANT_BITE = plantBite;
        this.foodLevel = foodLevel;
    }

    protected void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    protected boolean canBreed() {
        return age >= BREEDING_AGE;
    }

    protected int breed() {
        return (canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) ? rand.nextInt(MAX_LITTER_SIZE) + 1 : 0;
    }

    protected Location findFood(Field field) {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        for (Location loc : adjacent) {
            Animal animal = field.getAnimalAt(loc);
            if (animal instanceof Plant plant && plant.isAlive()) {
                foodLevel = plant.eaten(PLANT_BITE);
                return loc;
            }
        }
        return null;
    }


}
