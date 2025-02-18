import java.util.Random;

/**
 * Common elements for all animals - plants and consumers
 *
 * @author David J. Barnes, Michael Kölling and Krish Shah
 * @version 7.0
 */
public abstract class Animal {
    private static final Random rand = Randomizer.getRandom();
    // The animal's life expectancy
    protected int lifeExpectancy;
    // The animal's current age
    protected int age;
    // The animal's gender, true if male, false if female
    protected boolean isMale;
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's position.
    private Location location;

    /**
     * Constructor for objects of class Animal.
     *
     * @param location The animal's location.
     */
    public Animal(Location location) {
        this.alive = true;
        this.location = location;
        this.age = 0;
        this.lifeExpectancy = 0;
        this.isMale = rand.nextBoolean(); // relatively equal chance of male or female
    }

    /**
     * Checks if the animal might be resting/sleeping during nighttime.
     * Used to also determine the weather.
     * Valid daytime hours are 6am to 7pm.
     *
     * @param time The current time.
     * @return true if it is day, false if it is night.
     */
    public static boolean validTime(int time) {
        // between 6am to 7pm
        return (time > 6) && (time < 19);
    }

    /**
     * Increases the age.
     * This could result in the animal's death.
     */
    protected void incrementAge() {
        age++;
        if (age > lifeExpectancy) {
            setDead();
        }
    }

    /**
     * Act.
     *
     * @param currentField   The current state of the field.
     * @param nextFieldState The new state being built.
     * @param time           The current time of the environment, which impacts behaviour
     * @param isSunny        The weather condition, which impacts behaviour
     */
    abstract public void act(Field currentField, Field nextFieldState, int time, boolean isSunny);

    /**
     * Check whether the animal is alive or not.
     *
     * @return true if the animal is still alive.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Indicates that the animal is no longer alive.
     */
    protected void setDead() {
        alive = false;
        location = null;
    }

    /**
     * Return the animal's location.
     *
     * @return The animal's location.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Set the animal's location.
     *
     * @param location The new location.
     */
    protected void setLocation(Location location) {
        this.location = location;
    }
}
