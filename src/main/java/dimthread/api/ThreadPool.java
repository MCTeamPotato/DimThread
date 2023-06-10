package dimthread.api;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

public class ThreadPool {
    private ThreadPoolExecutor executor;
    private final int threadCount;
    private final IntLatch activeCount = new IntLatch();

    public ThreadPool(int threadCount) {
        this.threadCount = threadCount;
        this.restart();
    }

    public int getActiveCount() {
        return this.activeCount.getCount();
    }

    public void execute(Runnable action) {
        this.activeCount.increment();

        this.executor.execute(() -> {
            try {
                action.run();
            } finally {
                this.activeCount.decrement();
            }
        });
    }

    public <T> void execute(Iterator<T> iterator, Consumer<T> action) {
        iterator.forEachRemaining(t -> this.execute(() -> action.accept(t)));
    }

    public void awaitCompletion() {
        this.waitFor(value -> value == 0);
    }

    public void waitFor(IntPredicate condition) {
        try {
            this.activeCount.waitUntil(condition);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void restart() {
        if(this.executor == null || this.executor.isShutdown()) {
            this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(this.threadCount);
        }
    }

    public void shutdown() {
        this.executor.shutdown();
    }

    private static class IntLatch {
        private CountDownLatch latch;

        private IntLatch() {
            this(0);
        }

        @SuppressWarnings("SameParameterValue")
        private IntLatch(int count) {
            this.latch = new CountDownLatch(count);
        }

        private synchronized int getCount() {
            return (int)this.latch.getCount();
        }

        private synchronized void decrement() {
            this.latch.countDown();
            this.notifyAll();
        }

        private synchronized void increment() {
            this.latch = new CountDownLatch((int)this.latch.getCount() + 1);
            this.notifyAll();
        }

        @SuppressWarnings("BlockingMethodInNonBlockingContext")
        private synchronized void waitUntil(IntPredicate predicate) throws InterruptedException {
            while(!predicate.test(this.getCount())) {
                this.wait();
            }
        }
    }
}