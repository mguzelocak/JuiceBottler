import com.sun.tools.javac.Main;

public class Fetcher implements Runnable {
    // Thread instance to handle fetching in a separate thread
    private final Thread thread;
    // Counter to track the number of oranges fetched
    private int orangesFetched;
    // Flag to control whether the thread should continue working
    private volatile boolean timeToWork;

    private final BlockingMailBox fetchedMailBox;

    // Constructor to initialize the Fetcher with a thread number and an Orange instance
    Fetcher(BlockingMailBox mailBox) {
        this.fetchedMailBox = mailBox;
        orangesFetched = 0;
        // Create a new thread and assign a unique name using threadNum
        thread = new Thread(this, "Fetcher ");
    }

    // Method to stop fetching by setting the flag to false
    public void stopFetcher() {
        timeToWork = false;
        while (!fetchedMailBox.isEmpty()) {
            fetchedMailBox.get();
        }
        fetchedMailBox.put(null);

    }

    // Method to start fetching by setting the flag to true and starting the thread
    public void startFetcher() {
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
        System.out.println(Thread.currentThread().getName() + " Fetching oranges ");
        // Continue fetching while timeToWork is true
        while (timeToWork) {
//            fetchOrange(orange);
            Orange orange = new Orange();
            orange.runProcess();
            fetchedMailBox.put(orange);
            orangesFetched++;
        }
        System.out.println(Thread.currentThread().getName() + " Done");
    }

    // Method to fetch an orange if it is not already fetched
//    public void fetchOrange(Orange o) {
//        // Ensures that the orange is processed only if it is in a fetched state
//        while (o.getState() == Orange.State.Fetched) {
//            o.runProcess();
//        }
//        orangesFetched++; // Increment the counter after processing the orange
//    }

    // Getter method to return the number of fetched oranges
    public int getOrangesFetched() {
        return orangesFetched;
    }

    public boolean isFetchedMailBox() {
        return fetchedMailBox.isEmpty();
    }
//    public BlockingMailBox setMailBox(Orange o) {
//        mailBox.put(o);
//        return mailBox;
//    }

//    public Orange getOrange() {
//        return mailBox.get();
//    }
}
