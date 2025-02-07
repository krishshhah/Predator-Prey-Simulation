import java.util.List;
import java.util.Random;

/**
 * A simple predator-prey simulator, based on a rectangular field containing
 * rabbits and foxes.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Simulator {
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a fox will be created in any given grid position.
    private static final double SHARK_CREATION_PROBABILITY = 0.02;
    // The probability that a rabbit will be created in any given position.
    private static final double TURTLE_CREATION_PROBABILITY = 0.08;

    private static final double CROCODILE_CREATION_PROBABILITY = 0.04;

    private static final double ORCA_CREATION_PROBABILITY = 0.01;

    private static final double PLANT_CREATION_PROBABILITY = 0.05;

    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private final SimulatorView view;

    private int time;

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
            delay(50);         // adjust this to change execution speed
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each fox and rabbit.
     */
    public void simulateOneStep() {
        step++;
        incrementTime();
        // Use a separate Field to store the starting state of
        // the next step.
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());

        List<Animal> animals = field.getAnimals();
        for (Animal anAnimal : animals) {
            anAnimal.act(field, nextFieldState, time);
        }

        // Replace the old state with the new one.
        field = nextFieldState;

        reportStats();
        view.showStatus(step, field, displayTime());
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        time = 1;
        populate();
        view.showStatus(step, field, displayTime());
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
                if (rand.nextDouble() <= SHARK_CREATION_PROBABILITY) {
                    animal = new Shark(true, location);
                } else if (rand.nextDouble() <= TURTLE_CREATION_PROBABILITY) {
                    animal = new Turtle(true, location);
                } else if (rand.nextDouble() <= CROCODILE_CREATION_PROBABILITY) {
                    animal = new Crocodile(true, location);
                } else if (false && rand.nextDouble() <= ORCA_CREATION_PROBABILITY) {
                    animal = new Orca(true, location);
                } else if (rand.nextDouble() <= PLANT_CREATION_PROBABILITY) {
                    animal = new Plant(true, location);
                }
                if (animal != null)
                    field.placeAnimal(animal, location);
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

    private void incrementTime() {
        time = (time + 1) % 24;
    }

    private String displayTime() {
        int displayHour = (time == 0 || time == 12) ? 12 : time % 12;
        String period = (time < 12) ? "am" : "pm";
        return displayHour + period;
    }

}
