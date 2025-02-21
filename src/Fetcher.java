/**
 * Description: The {@code Fetcher} class is responsible for fetching oranges in a separate thread.
 * It continuously creates and processes oranges, storing them in a shared mailbox.
 * This class implements the {@code Runnable} interface to allow execution in a separate thread.
 *
 * @author mcguzelocak
 * Date: 02/20/2025
 */
public class Fetcher implements Runnable {

    /**
     * Thread instance to handle fetching in a separate thread.
     */
    private final Thread thread;
    /**
     * Shared mailbox to store fetched oranges.
     */
    private final BlockingMailBox fetchedMailBox;
    /**
     * Counter to track the number of oranges fetched.
     */
    private int orangesFetched;
    /**
     * Flag to control whether the thread should continue working.
     */
    private volatile boolean timeToWork;

    /**
     * Constructs a new Fetcher with a specified mailbox.
     * Initializes the thread and sets the initial count of fetched oranges to zero.
     *
     * @param mailBox The shared mailbox where fetched oranges will be stored.
     */
    Fetcher(BlockingMailBox mailBox) {
        this.fetchedMailBox = mailBox;
        orangesFetched = 0;
        thread = new Thread(this, "Fetcher");
    }

    /**
     * Stops the fetching process by setting the flag to {@code false}.
     * Clears the mailbox and inserts a {@code null} value to indicate termination.
     */
    public void stopFetcher() {
        timeToWork = false;
        while (!fetchedMailBox.isEmpty()) {
            fetchedMailBox.get();
        }
        fetchedMailBox.put(null);
    }

    /**
     * Starts the fetching process by setting the flag to {@code true} and starting the thread.
     */
    public void startFetcher() {
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
     * Defines the main execution logic for the fetching thread.
     * Continuously creates and processes oranges while {@code timeToWork} is {@code true}.
     */
    @Override
    public void run() {
        while (timeToWork) {
            Orange orange = new Orange();
            orange.runProcess();
            fetchedMailBox.put(orange);
            orangesFetched++;
        }
    }

    /**
     * Gets the total number of oranges fetched.
     *
     * @return The count of fetched oranges.
     */
    public int getOrangesFetched() {
        return orangesFetched;
    }

    /**
     * Checks if the mailbox is empty.
     *
     * @return {@code true} if the mailbox is empty, {@code false} otherwise.
     */
    public boolean isFetchedMailBox() {
        return fetchedMailBox.isEmpty();
    }
}