/**
 * The {@code BlockingMailBox} class is a synchronized mailbox for passing an {@code Orange} object
 * between different processing stages in a thread-safe manner.
 * It ensures that only one orange is held at a time and uses wait-notify
 * for synchronization between producer and consumer threads.
 */
public class BlockingMailBox {

    /**
     * The orange stored in the mailbox.
     */
    private Orange orange;

    /**
     * Constructs an empty BlockingMailBox.
     */
    public BlockingMailBox() {
        orange = null;
    }

    /**
     * Places an orange into the mailbox. If the mailbox is already occupied,
     * the calling thread waits until it becomes available.
     *
     * @param o The orange to be placed into the mailbox.
     */
    public synchronized void put(Orange o) {
        while (!isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ignored) {
                // Thread interrupted, safely exit.
            }
        }
        orange = o;
        notifyAll(); // Notify waiting threads that an orange is available.
    }

    /**
     * Retrieves an orange from the mailbox. If the mailbox is empty,
     * the calling thread waits until an orange is available.
     *
     * @return The orange retrieved from the mailbox.
     */
    public synchronized Orange get() {
        while (isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ignored) {
                // Thread interrupted, safely exit.
            }
        }
        Orange ret = orange;
        orange = null;
        notifyAll(); // Notify waiting threads that the mailbox is empty.
        return ret;
    }

    /**
     * Checks whether the mailbox is empty.
     *
     * @return {@code true} if the mailbox is empty, {@code false} otherwise.
     */
    public synchronized boolean isEmpty() {
        return orange == null;
    }
}
