/**
 * Description: The {@code Orange} class represents an orange that undergoes multiple processing stages.
 * The processing includes fetching, peeling, squeezing, bottling, and final processing.
 * Each stage has a predefined time required to complete before moving to the next stage.
 *
 * @author nwilliams
 * Date: 02/21/2025
 */
public class Orange {

    /**
     * Current state of the orange in the processing cycle.
     */
    private State state;

    /**
     * Constructs a new {@code Orange} and initializes it in the fetched state.
     * The initial processing work is performed immediately.
     */
    public Orange() {
        state = State.Fetched;
        doWork();
    }

    /**
     * Retrieves the current state of the orange.
     *
     * @return The current processing state of the orange.
     */
    public State getState() {
        return state;
    }

    /**
     * Processes the orange to move it to the next state.
     *
     * @throws IllegalStateException If the orange has already been completely processed.
     */
    public void runProcess() {
        if (state == State.Processed) {
            throw new IllegalStateException("This orange has already been processed");
        }
        doWork();
        state = state.getNext();
    }

    /**
     * Simulates the processing time by making the thread sleep for the necessary duration.
     * If interrupted, an error message is displayed.
     */
    private void doWork() {
        try {
            Thread.sleep(state.timeToComplete);
        } catch (InterruptedException e) {
            System.err.println("Incomplete orange processing, juice may be bad");
        }
    }

    /**
     * Enum representing different states of orange processing.
     * Each state has an associated time to complete processing.
     */
    public enum State {
        Fetched(15),
        Peeled(38),
        Squeezed(29),
        Bottled(17),
        Processed(1);

        /**
         * Index of the final processing state.
         */
        private static final int finalIndex = State.values().length - 1;

        /**
         * Time required (in milliseconds) to complete the processing step.
         */
        final int timeToComplete;

        /**
         * Constructor to associate a processing time with each state.
         *
         * @param timeToComplete Time in milliseconds to complete the processing step.
         */
        State(int timeToComplete) {
            this.timeToComplete = timeToComplete;
        }

        /**
         * Retrieves the next processing state.
         *
         * @return The next {@code State} in the processing sequence.
         * @throws IllegalStateException If the orange is already in the final state.
         */
        State getNext() {
            int currIndex = this.ordinal();
            if (currIndex >= finalIndex) {
                throw new IllegalStateException("Already at final state");
            }
            return State.values()[currIndex + 1];
        }
    }
}
