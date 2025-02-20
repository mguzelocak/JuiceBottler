/**
 * The {@code Plant} class simulates an orange juice processing plant.
 * It consists of multiple stages: fetching, peeling, squeezing, and bottling oranges.
 * Each stage runs in a separate thread, and the plant processes oranges for a fixed duration.
 *
 * The class also provides methods to start, stop, and summarize the processing results.
 */

public class Plant implements Runnable {

    /**
     * Duration for which the juice processing plant runs.
     */
    public static final long PROCESSING_TIME = 5 * 1000;

    /**
     * Number of plant instances.
     */
    private static final int NUM_PLANTS = 2;

    /**
     * Shared mailboxes for different stages of processing.
     */
    private final BlockingMailBox fetchedMailBox = new BlockingMailBox();
    private final BlockingMailBox peeledMailBox = new BlockingMailBox();
    private final BlockingMailBox squeezedMailBox = new BlockingMailBox();

    /**
     * Processing components: Fetcher, Peeler, Squeezer, and Bottler.
     */
    private Fetcher fetcher = new Fetcher(fetchedMailBox);
    private Peeler peeler = new Peeler(fetchedMailBox, peeledMailBox);
    private Squeezer squeezer = new Squeezer(peeledMailBox, squeezedMailBox);
    private Bottler bottler = new Bottler(squeezedMailBox);

    /**
     * Entry point to start multiple plants and summarize results.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Plant[] plants = new Plant[NUM_PLANTS];
        for (int i = 0; i < NUM_PLANTS; i++) {
            plants[i] = new Plant(i);
            plants[i].startPlant();
        }

        delay(PROCESSING_TIME, "Plant malfunction");

        for (Plant p : plants) {
            p.stopPlant();
        }
        for (Plant p : plants) {
            p.waitToStop();
        }

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
        System.out.println("Created " + totalBottles + ", wasted " + totalWasted + " oranges");
    }

    /**
     * Delays execution for a specified time.
     *
     * @param time Duration in milliseconds.
     * @param errMsg Error message in case of interruption.
     */
    private static void delay(long time, String errMsg) {
        long sleepTime = Math.max(1, time);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.err.println(errMsg);
        }
    }

    /**
     * Oranges required to produce one bottle of juice.
     */
    public final int ORANGES_PER_BOTTLE = 3;

    /**
     * Processing thread for the plant.
     */
    final Thread thread;
    private int orangesProvided;
    private int orangesProcessed;
    private volatile boolean timeToWork;

    /**
     * Constructs a new Plant instance.
     *
     * @param threadNum Identifier for the plant thread.
     */
    Plant(int threadNum) {
        fetcher = new Fetcher(fetchedMailBox);
        peeler = new Peeler(fetchedMailBox, peeledMailBox);
        squeezer = new Squeezer(peeledMailBox, squeezedMailBox);
        bottler = new Bottler(squeezedMailBox);

        orangesProvided = 0;
        orangesProcessed = 0;
        thread = new Thread(this, "Plant[" + threadNum + "]");
    }

    /**
     * Starts the plant's processing.
     */
    public void startPlant() {
        timeToWork = true;
        thread.start();
    }

    /**
     * Stops the plant's processing.
     */
    public void stopPlant() {
        timeToWork = false;
    }

    /**
     * Waits for the plant's thread to stop execution.
     */
    public void waitToStop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println(thread.getName() + " stop malfunction");
        }
    }

    /**
     * Executes the plant's processing cycle.
     */
    public void run() {
        System.out.println(Thread.currentThread().getName() + " Processing oranges ");
        fetcher.startFetcher();
        peeler.startPeeler();
        squeezer.startSqueezer();
        bottler.startBottler();

        long startTime = System.currentTimeMillis();
        long runDuration = 10000;
        while (System.currentTimeMillis() - startTime < runDuration) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {}
        }

        fetchedMailBox.put(null);
        peeledMailBox.put(null);
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
