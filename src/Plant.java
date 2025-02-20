import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Plant implements Runnable {
    // How long do we want to run the juice processing
    public static final long PROCESSING_TIME = 5 * 1000;

    private static final int NUM_PLANTS = 2;
    private final BlockingMailBox fetchedMailBox = new BlockingMailBox();
    private final BlockingMailBox peeledMailBox = new BlockingMailBox();
    private final BlockingMailBox squeezedMailBox = new BlockingMailBox();


    private Fetcher fetcher = new Fetcher(fetchedMailBox);
    private Peeler peeler = new Peeler(fetchedMailBox, peeledMailBox);
    private Squeezer squeezer = new Squeezer(peeledMailBox, squeezedMailBox);
    private Bottler bottler = new Bottler(squeezedMailBox);


    public static void main(String[] args) {
        // Startup the plants
        Plant[] plants = new Plant[NUM_PLANTS];
        for (int i = 0; i < NUM_PLANTS; i++) {
            plants[i] = new Plant(i);
            plants[i].startPlant();
        }

        // Give the plants time to do work
        delay(PROCESSING_TIME, "Plant malfunction");

        // Stop the plant, and wait for it to shutdown
        for (Plant p : plants) {
            p.stopPlant();
        }
        for (Plant p : plants) {
            p.waitToStop();
        }

        // Summarize the results
        int totalProvided = 0;
        int totalProcessed = 0;
        int totalBottles = 0;
        int totalWasted = 0;
        for (Plant p : plants) {
            totalProvided += p.getProvidedOranges();
            totalProcessed += p.getProcessedOranges();
            totalBottles += p.getBottles();
            totalWasted += p.getWaste();
        }
        System.out.println("Total provided/processed = " + totalProvided + "/" + totalProcessed);
        System.out.println("Created " + totalBottles +
                ", wasted " + totalWasted + " oranges");
    }

    private static void delay(long time, String errMsg) {
        long sleepTime = Math.max(1, time);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.err.println(errMsg);
        }
    }

    public final int ORANGES_PER_BOTTLE = 3;

    final Thread thread;
    private int orangesProvided;
    private int orangesProcessed;
    private volatile boolean timeToWork;

    Plant(int threadNum) {
        fetcher = new Fetcher(fetchedMailBox);
        peeler = new Peeler(fetchedMailBox, peeledMailBox);
        squeezer = new Squeezer(peeledMailBox, squeezedMailBox);
        bottler = new Bottler(squeezedMailBox);

        orangesProvided = 0;
        orangesProcessed = 0;
        thread = new Thread(this, "Plant[" + threadNum + "]");
    }

    public void startPlant() {
        timeToWork = true;
        thread.start();
    }

    public void stopPlant() {
        timeToWork = false;
    }

    public void waitToStop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println(thread.getName() + " stop malfunction");
        }

    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " Processing oranges ");
        fetcher.startFetcher();
        peeler.startPeeler();
        squeezer.startSqueezer();
        bottler.startBottler();

        long startTime = System.currentTimeMillis();
        long runDuration = 10000; // 10 seconds

        while (System.currentTimeMillis() - startTime < runDuration) {
            try {
                Thread.sleep(1); // 🔹 Wait to prevent high CPU usage
            } catch (InterruptedException ignored) {}
        }
        fetchedMailBox.put(null); // Signal Fetcher to stop
        peeledMailBox.put(null); // Signal Peeler to stop
        squeezedMailBox.put(null);
        fetcher.stopFetcher();
        peeler.stopPeeler();
        squeezer.stopSqueezer();
        bottler.stopBottler();
        fetcher.waitToStop();
        peeler.waitToStop();
        squeezer.waitToStop();
        bottler.waitToStop();
        orangesProvided += fetcher.getOrangesFetched();
        orangesProcessed += bottler.getOrangesBottled();
        System.out.println(Thread.currentThread().getName() + " Done");
    }

    public int getProvidedOranges() {
        return orangesProvided;
    }

    public int getProcessedOranges() {
        return orangesProcessed;
    }

    public int getBottles() {
        return orangesProcessed / ORANGES_PER_BOTTLE;
    }

    public int getWaste() {
        return orangesProcessed % ORANGES_PER_BOTTLE;
    }
}
