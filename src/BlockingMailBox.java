public class BlockingMailBox {
    private Orange orange;

    public BlockingMailBox() {
        orange = null;
    }

    public synchronized void put(Orange o) {
        while (!isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ignored) {
//                return;
            }
        }
        orange = o;
        notifyAll();
    }

    public synchronized Orange get() {
        while (isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ignored) {
//                return null;
            }
        }
        Orange ret = orange;
        orange = null;
        notifyAll();
        return ret;
    }

    public synchronized boolean isEmpty() {
        return orange == null;
    }
}
