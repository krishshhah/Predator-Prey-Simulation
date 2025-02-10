public abstract class Predator extends Animal {
    protected final int BREEDING_AGE;
    protected final int MAX_AGE;
    protected final double BREEDING_PROBABILITY;
    protected final int MAX_LITTER_SIZE;
    protected final int TURTLE_FOOD_VALUE;
    protected final int CROCODILE_FOOD_VALUE;

    public Predator(Location location, int breedingAge, int maxAge, double breedingProbability, int maxLitterSize, int turtleFoodValue, int crocodileFoodValue) {
        super(location);
        this.BREEDING_AGE = breedingAge;
        this.MAX_AGE = maxAge;
        this.BREEDING_PROBABILITY = breedingProbability;
        this.MAX_LITTER_SIZE = maxLitterSize;
        this.TURTLE_FOOD_VALUE = turtleFoodValue;
        this.CROCODILE_FOOD_VALUE = crocodileFoodValue;
    }


}
