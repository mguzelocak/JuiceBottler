/**
 * Description: The {@code Peeler} class is responsible for peeling oranges in a separate thread.
 * It retrieves oranges from a fetched mailbox, processes them, and stores them in a peeled mailbox.
 * This class implements the {@code Runnable} interface to allow execution in a separate thread.
 *
 * @author mcguzelocak
 * Date: 02/21/2025
 */
public class Peeler implements Runnable {

    /**
     * Thread instance to handle peeling in a separate thread.
     */
    private final Thread thread;
    /**
     * Shared mailbox from which fetched oranges are retrieved.
     */
    private final BlockingMailBox fetchedMailBox;
    /**
     * Shared mailbox where peeled oranges are stored.
     */
    private final BlockingMailBox peeledMailBox;
    /**
     * Counter to track the number of oranges peeled.
     */
    private int orangesPeeled;
    /**
     * Flag to control whether the thread should continue working.
     */
    private volatile boolean timeToWork;

    /**
     * Constructs a new Peeler with specified mailboxes.
     * Initializes the thread and sets the initial count of peeled oranges to zero.
     *
     * @param fetchedMailBox  The mailbox containing fetched oranges.
     * @param squeezedMailBox The mailbox where peeled oranges will be stored.
     */
    Peeler(BlockingMailBox fetchedMailBox, BlockingMailBox squeezedMailBox) {
        this.fetchedMailBox = fetchedMailBox;
        this.peeledMailBox = squeezedMailBox;
        orangesPeeled = 0;
        thread = new Thread(this, "Peeler");
    }

    /**
     * Stops the peeling process by setting the flag to {@code false}.
     * Clears the peeled mailbox and inserts a {@code null} value to indicate termination.
     */
    public void stopPeeler() {
        timeToWork = false;
        while (!peeledMailBox.isEmpty()) {
            peeledMailBox.get();
        }
        peeledMailBox.put(null);
    }

    /**
     * Starts the peeling process by setting the flag to {@code true} and starting the thread.
     */
    public void startPeeler() {
        timeToWork = true;
        thread.start();
    }

    /**
     * Waits for the thread to stop execution by joining it.
     * If the thread is interrupted, an error message is printed.
     */
    public void waitToStop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println(thread.getName() + " stop malfunction");
        }
    }

    /**
     * Defines the main execution logic for the peeling thread.
     * Continuously retrieves and peels oranges while {@code timeToWork} is {@code true}.
     */
    @Override
    public void run() {
        while (timeToWork) {
            Orange orange = fetchedMailBox.get();
            peelOrange(orange);
        }
    }

    /**
     * Peels an orange and moves it to the peeled mailbox if it is in the fetched state.
     *
     * @param o The orange to be peeled.
     */
    public void peelOrange(Orange o) {
        while (o.getState() == Orange.State.Peeled) {
            o.runProcess();
            peeledMailBox.put(o);
        }
        orangesPeeled++; // Increment the counter after processing the orange
    }

    /**
     * Gets the total number of oranges peeled.
     *
     * @return The count of peeled oranges.
     */
    public int getOrangesPeeled() {
        return orangesPeeled;
    }
}
