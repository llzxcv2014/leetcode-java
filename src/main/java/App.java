import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class App {

    private static final int CHANNEL_NUM = 8;
    private static final int CHANNEL_BUFFER_SIZE = 65536;

    /*
     * reference: http://ericfu.me/perf-test-blockingqueue-vs-channel/
     * 测试方案：
     *
     * 1. 主线程将一个 Integer 对象发到 Channel 0
     * 2. 线程 i 将对象从 Channel i 不断搬运到 Channel i+1
     * 3. 最后一个线程从 Channel N-1 中拿到对象，做加和
     *
     * 为了保证公平，Go 中自行封装一个 Integer 而不是用 int 型；考虑到实际中大多数情况下 channel 里走的都是对象而非基本类型，这样是合理的。
     * 发现二者完成时间基本都在 3.8s ~ 4.0s 之间，可以说没有差异。 ArrayBlockingQueue 的性能看来还是很高的。
     *
     * PS. 尝试了容量不限的 ListBlockingQueue，时间在 5s 左右，也还可以接受。
     */
    public static void main(String[] args) throws InterruptedException {
        List<BlockingQueue<Integer>> channels = new ArrayList<>(CHANNEL_NUM);
        channels.add(new ArrayBlockingQueue<>(CHANNEL_BUFFER_SIZE));
        for (int i = 1; i < CHANNEL_NUM; i++) {
            channels.add(new ArrayBlockingQueue<>(CHANNEL_BUFFER_SIZE));
            new Thread(new Adder(i, channels.get(i - 1), channels.get(i))).start();
        }
        long startTime = System.currentTimeMillis();
        Thread threadEnder = new Thread(new Ender(channels.get(CHANNEL_NUM - 1)));
        threadEnder.start();
        BlockingQueue<Integer> initChannel = channels.get(0);
        for (int x = 0; x < 10000000; x++) {
            initChannel.put(x);
        }
        initChannel.put(-1);
        threadEnder.join();
        System.out.println((System.currentTimeMillis() - startTime) / 1000.0);
    }

    static class Adder implements Runnable {
        private final int no;
        private final BlockingQueue<Integer> inputQueue;
        private final BlockingQueue<Integer> outputQueue;

        public Adder(int no, BlockingQueue<Integer> inputQueue, BlockingQueue<Integer> outputQueue) {
            this.no = no;
            this.inputQueue = inputQueue;
            this.outputQueue = outputQueue;
        }

        @Override
        public void run() {
            Integer x;
            try {
                do {
                    x = inputQueue.take();
                    outputQueue.put(x);
                } while (x != -1);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("Adder " + no + " exited");
        }
    }

    static class Ender implements Runnable {
        private final BlockingQueue<Integer> inputQueue;

        public Ender(BlockingQueue<Integer> inputQueue) {
            this.inputQueue = inputQueue;
        }

        @Override
        public void run() {
            long sum = 0;
            Integer x;
            try {
                while (true) {
                    x = inputQueue.take();
                    if (x != -1) {
                        sum += x;
                    } else {
                        break;
                    }
                }
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("Ender exited, sum = " + sum);
        }
    }
}
