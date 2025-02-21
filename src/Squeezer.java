/**
 * Description: The {@code Squeezer} class is responsible for squeezing oranges in a separate thread.
 * It retrieves peeled oranges from a mailbox, processes them, and stores them in a squeezed mailbox.
 * This class implements the {@code Runnable} interface to allow execution in a separate thread.
 *
 * @author mcguzelocak
 * Date: 02/21/2025
 */
public class Squeezer implements Runnable {

    /**
     * Thread instance to handle squeezing in a separate thread.
     */
    private final Thread thread;
    /**
     * Shared mailbox from which peeled oranges are retrieved.
     */
    private final BlockingMailBox peeledMailBox;
    /**
     * Shared mailbox where squeezed oranges are stored.
     */
    private final BlockingMailBox squeezedMailBox;
    /**
     * Counter to track the number of oranges squeezed.
     */
    private int orangesSqueezed;
    /**
     * Flag to control whether the thread should continue working.
     */
    private volatile boolean timeToWork;

    /**
     * Constructs a new Squeezer with specified mailboxes.
     * Initializes the thread and sets the initial count of squeezed oranges to zero.
     *
     * @param peeledMailBox   The mailbox containing peeled oranges.
     * @param squeezedMailBox The mailbox where squeezed oranges will be stored.
     */
    Squeezer(BlockingMailBox peeledMailBox, BlockingMailBox squeezedMailBox) {
        orangesSqueezed = 0;
        this.peeledMailBox = peeledMailBox;
        this.squeezedMailBox = squeezedMailBox;
        thread = new Thread(this, "Squeezer");
    }

    /**
     * Stops the squeezing process by setting the flag to {@code false}.
     * Clears the squeezed mailbox and inserts a {@code null} value to indicate termination.
     */
    public void stopSqueezer() {
        timeToWork = false;
        while (!squeezedMailBox.isEmpty()) {
            squeezedMailBox.get();
        }
        squeezedMailBox.put(null);
    }

    /**
     * Starts the squeezing process by setting the flag to {@code true} and starting the thread.
     */
    public void startSqueezer() {
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
     * Defines the main execution logic for the squeezing thread.
     * Continuously retrieves and squeezes oranges while {@code timeToWork} is {@code true}.
     */
    @Override
    public void run() {
        while (timeToWork) {
            Orange orange = peeledMailBox.get();
            squeezeOrange(orange);
//            System.out.println(Thread.currentThread().getName() + " Squeezing oranges ");
        }
//        System.out.println(Thread.currentThread().getName() + " Done");
    }

    /**
     * Squeezes an orange and moves it to the squeezed mailbox if it is in the peeled state.
     *
     * @param o The orange to be squeezed.
     */
    public void squeezeOrange(Orange o) {
        while (o.getState() == Orange.State.Squeezed) {
            o.runProcess();
            squeezedMailBox.put(o);
        }
        orangesSqueezed++; // Increment the counter after processing the orange
    }

    /**
     * Gets the total number of oranges squeezed.
     *
     * @return The count of squeezed oranges.
     */
    public int getOrangesSqueezed() {
        return orangesSqueezed;
    }
}
