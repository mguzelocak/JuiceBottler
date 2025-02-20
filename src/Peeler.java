public class Peeler implements Runnable {
    // Thread instance to handle fetching in a separate thread
    private final Thread thread;
    // Counter to track the number of oranges fetched
    private int orangesPeeled;
    // Flag to control whether the thread should continue working
    private volatile boolean timeToWork;
    // Reference to an Orange object
//    private final Orange orange;

    private final BlockingMailBox fetchedMailBox;
    private final BlockingMailBox peeledMailBox;
    // Constructor to initialize the Fetcher with a thread number and an Orange instance
    Peeler(BlockingMailBox fetchedMailBox, BlockingMailBox squeezedMailBox) {
//        this.orange = mailBox.get();
        this.fetchedMailBox = fetchedMailBox;
        this.peeledMailBox = squeezedMailBox;
        orangesPeeled = 0;
        // Create a new thread and assign a unique name using threadNum
        thread = new Thread(this, "Peeler ");
    }

    // Method to stop fetching by setting the flag to false
    public void stopPeeler() {
        timeToWork = false;
        while (!peeledMailBox.isEmpty()) {
            peeledMailBox.get();
        }
        peeledMailBox.put(null);
    }

    // Method to start fetching by setting the flag to true and starting the thread
    public void startPeeler() {
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

        // Continue fetching while timeToWork is true
        while (timeToWork) {
            Orange orange = fetchedMailBox.get();
            peelOrange(orange);
            System.out.println(Thread.currentThread().getName() + " Peeling oranges ");
//            orangesPeeled++;
        }
        System.out.println(Thread.currentThread().getName() + " Done");
    }

    // Method to fetch an orange if it is not already fetched
    public void peelOrange(Orange o) {
        // Ensures that the orange is processed only if it is in a fetched state
        while (o.getState() == Orange.State.Peeled) {
            o.runProcess();
            peeledMailBox.put(o);
        }
        orangesPeeled++; // Increment the counter after processing the orange
    }

    // Getter method to return the number of fetched oranges
    public int getOrangesPeeled() {
        return orangesPeeled;
    }
}
