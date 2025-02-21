/**
 * Description: The {@code Bottler} class is responsible for bottling oranges in a separate thread.
 * It retrieves squeezed oranges from a mailbox, processes them, and updates the count of bottled oranges.
 * This class implements the {@code Runnable} interface to allow execution in a separate thread.
 *
 * @author mcguzelocak
 * Date: 02/21/2025
 */
public class Bottler implements Runnable {

    /**
     * Thread instance to handle bottling in a separate thread.
     */
    private final Thread thread;
    /**
     * Shared mailbox from which squeezed oranges are retrieved.
     */
    private final BlockingMailBox squeezedMailBox;
    /**
     * Counter to track the number of oranges bottled.
     */
    private int orangesBottled;
    /**
     * Flag to control whether the thread should continue working.
     */
    private volatile boolean timeToWork;

    /**
     * Constructs a new Bottler with a specified squeezed mailbox.
     * Initializes the thread and sets the initial count of bottled oranges to zero.
     *
     * @param squeezedMailBox The mailbox containing squeezed oranges.
     */
    Bottler(BlockingMailBox squeezedMailBox) {
        this.squeezedMailBox = squeezedMailBox;
        orangesBottled = 0;
        thread = new Thread(this, "Bottler");
    }

    /**
     * Stops the bottling process by setting the flag to {@code false}.
     */
    public void stopBottler() {
        timeToWork = false;
    }

    /**
     * Starts the bottling process by setting the flag to {@code true} and starting the thread.
     */
    public void startBottler() {
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
     * Defines the main execution logic for the bottling thread.
     * Continuously retrieves and bottles oranges while {@code timeToWork} is {@code true}.
     */
    @Override
    public void run() {
        while (timeToWork) {
            Orange orange = squeezedMailBox.get();
            bottleOrange(orange);
//            System.out.println(Thread.currentThread().getName() + " Bottling oranges ");
        }
//        System.out.println(Thread.currentThread().getName() + " Done");
    }

    /**
     * Bottles an orange and updates its state.
     *
     * @param o The orange to be bottled.
     */
    public void bottleOrange(Orange o) {
        while (o.getState() == Orange.State.Bottled) {
            o.runProcess();
        }
        orangesBottled++; // Increment the counter after processing the orange
    }

    /**
     * Gets the total number of oranges bottled.
     *
     * @return The count of bottled oranges.
     */
    public int getOrangesBottled() {
        return orangesBottled;
    }
}
