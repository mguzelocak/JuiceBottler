import java.util.Map;

public class Squeezer implements Runnable {
    // Thread instance to handle fetching in a separate thread
    private final Thread thread;
    // Counter to track the number of oranges fetched
    private int orangesSqueezed;
    // Flag to control whether the thread should continue working
    private volatile boolean timeToWork;
    // Reference to an Orange object
//    private final Orange orange;

//    public BlockingMailBox mailBox;
    private final BlockingMailBox peeledMailBox;
    private final BlockingMailBox squeezedMailBox;


    // Constructor to initialize the Fetcher with a thread number and an Orange instance
    Squeezer(BlockingMailBox peeledMailBox, BlockingMailBox squeezedMailBox) {
//        this.orange = mailBox.get();
        orangesSqueezed = 0;
        this.peeledMailBox = peeledMailBox;
        this.squeezedMailBox = squeezedMailBox;
        // Create a new thread and assign a unique name using threadNum
        thread = new Thread(this, "Squeezer ");
    }

    // Method to stop fetching by setting the flag to false
    public void stopSqueezer() {
        timeToWork = false;
        while (!squeezedMailBox.isEmpty()) {
            squeezedMailBox.get();
        }
        squeezedMailBox.put(null);

//        thread.interrupt();
    }

    // Method to start fetching by setting the flag to true and starting the thread
    public void startSqueezer() {
        timeToWork = true;
        thread.start();
    }

    // Method to wait until the thread stops execution
    public void waitToStop() {
        try {
            thread.join(); // Ensures that the calling thread waits for this thread to finish
        } catch (InterruptedException e) {
            System.err.println(thread.getName() + " stop malfunction"); // Handles interruptions
        }
    }

    // The run method defines what the thread will execute
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Squeezing oranges ");
        // Continue fetching while timeToWork is true
        while (timeToWork) {
            Orange orange = peeledMailBox.get();
//            if (orange == null) break;
            squeezeOrange(orange);
//            mailBox.put(orange);
            orangesSqueezed++;
        }
        System.out.println(Thread.currentThread().getName() + " Done");
    }

    // Method to fetch an orange if it is not already fetched
    public void squeezeOrange(Orange o) {
        // Ensures that the orange is processed only if it is in a fetched state
        while (o.getState() == Orange.State.Squeezed) {
            o.runProcess();
            squeezedMailBox.put(o);
        }
        orangesSqueezed++; // Increment the counter after processing the orange
    }

    // Getter method to return the number of fetched oranges
    public int getOrangesSqueezed() {
        return orangesSqueezed;
    }
}
