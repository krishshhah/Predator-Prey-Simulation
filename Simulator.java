import java.util.List;
import java.util.Random;

/**
 * A simple predator-prey simulator, based on a rectangular field containing
 * producers, prey, and predators.
 *
 * @author David J. Barnes, Michael Kölling and Krish Shah
 * @version 7.4
 */
public class Simulator {
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 130;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 85;
    // The probability that a Shark will be created in any given grid position.
    private static final double SHARK_CREATION_PROBABILITY = 0.05;
    // The probability that a Turtle will be created in any given grid position.
    private static final double TURTLE_CREATION_PROBABILITY = 0.15;
    // The probability that an Iguana will be created in any given grid position.
    private static final double IGUANA_CREATION_PROBABILITY = 0.25;
    // The probability that an Orca will be created in any given grid position.
    private static final double ORCA_CREATION_PROBABILITY = 0.04;
    // The probability that a Plant will be created in any given grid position.
    private static final double PLANT_CREATION_PROBABILITY = 0.21;
    // The probability that a Manatee will be created in any given grid position.
    private static final double MANATEE_CREATION_PROBABILITY = 0.17;
    // For random probability behaviour.
    private static final Random rand = Randomizer.getRandom();
    // A graphical view of the simulation.
    private final SimulatorView view;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // The current time of the simulation's environment.
    private int time;
    // The current weather conditions: either sunny or rainy/cloudy
    private boolean isSunny;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator() {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     *
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        if (width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be >= zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        field = new Field(depth, width);
        view = new SimulatorView(depth, width);
        time = 0;
        isSunny = true;

        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long
     * period (4000 steps).
     */
    public void runLongSimulation() {
        simulate(700);
    }

    /**
     * Run the simulation for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     *
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps) {
        reportStats();
        for (int n = 1; n <= numSteps && field.isViable(); n++) {
            simulateOneStep();
            delay(100);         // adjust this to change execution speed
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each animal.
     */
    public void simulateOneStep() {
        step++;
        incrementTime();
        isSunny = weatherChange();
        // Use a separate Field to store the starting state of
        // the next step.
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());

        List<Animal> animals = field.getAnimals();
        for (Animal anAnimal : animals) {
            anAnimal.act(field, nextFieldState, time, isSunny); // all animals behave differently during hours of the day
        }

        // Replace the old state with the new one.
        field = nextFieldState;

        reportStats();
        view.showStatus(step, field, displayTime(), time, displayWeather());
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        time = 1;
        populate();
        view.showStatus(step, field, displayTime(), time, displayWeather());
    }

    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate() {
        Random rand = Randomizer.getRandom();
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Animal animal = null;
                if (rand.nextDouble() <= PLANT_CREATION_PROBABILITY) {
                    animal = new Plant(true, location);
                } else if (rand.nextDouble() <= IGUANA_CREATION_PROBABILITY) {
                    animal = new Iguana(true, location);
                } else if (rand.nextDouble() <= ORCA_CREATION_PROBABILITY) {
                    animal = new Orca(true, location);
                } else if (rand.nextDouble() <= TURTLE_CREATION_PROBABILITY) {
                    animal = new Turtle(true, location);
                } else if (rand.nextDouble() <= SHARK_CREATION_PROBABILITY) {
                    animal = new Shark(true, location);
                } else if (rand.nextDouble() <= MANATEE_CREATION_PROBABILITY) {
                    animal = new Manatee(true, location);
                }
                if (animal != null) field.placeAnimal(animal, location);
            }
        }
    }

    /**
     * Report on the number of each type of animal in the field.
     */
    public void reportStats() {
        //System.out.print("Step: " + step + " ");
        field.fieldStats();
    }

    /**
     * Pause for a given time.
     *
     * @param milliseconds The time to pause for, in milliseconds
     */
    private void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * Increments the time hourly.
     * If it reaches 24 hours, the time reverts back to 0.
     */
    private void incrementTime() {
        time = (time + 1) % 24;
    }

    /**
     * Changes the way the time is displayed, based on AM or PM clocks.
     *
     * @return a string displaying the current time.
     */
    private String displayTime() {
        int displayHour = (time == 0 || time == 12) ? 12 : time % 12;
        String period = (time < 12) ? " am" : " pm";
        return displayHour + period;
    }

    /**
     * Changes the weather based on probabilities.
     * Similar to a Markov Chain/Transition Matrix
     * <p>
     * S       R
     * S [0.9     0.1]
     * <p>
     * R [0.8     0.2]
     *
     * @return True if it will continue being sunny.
     */
    private boolean weatherChange() {
        // if sunny, 90% it is sunny again next day
        if (isSunny) return rand.nextDouble() < 0.9;
        // if cloudy/rainy, 80% it is sunny the next day
        return rand.nextDouble() < 0.8;
    }

    /**
     * Displays the current weather of the environment.
     *
     * @return a string displaying the current weather.
     */
    private String displayWeather() {
        if (!Animal.validTime(time)) return "Night";
        if (isSunny) return "Sunny";
        return "Cloudy";
    }


}
