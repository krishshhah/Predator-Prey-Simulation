/**
 * Common elements of foxes and rabbits.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public abstract class Animal {
    protected int lifeExpectancy;
    protected int age;
    // The Turtle's gender, true if isMale, false if female
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
    }


    /**
     * Increase the age.
     * This could result in the Turtle's death.
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
     */
    abstract public void act(Field currentField, Field nextFieldState, int time);

    /**
     * Check whether the animal is alive or not.
     *
     * @return true if the animal is still alive.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
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

    /**
     * Checks if the animal might be resting/sleeping during nighttime.
     *
     * @param time The current time.
     */
    protected static boolean validTime(int time) {
        // between 6am to 7pm
        return (time > 6) && (time < 19);
    }
}
