package dimthread.api;

import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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

    @SuppressWarnings("SameParameterValue")
    private static class IntLatch {
        private final Lock lock = new ReentrantLock();
        private final Condition condition = lock.newCondition();
        private int count;

        private IntLatch() {
            this(0);
        }

        private IntLatch(int count) {
            this.count = count;
        }

        private int getCount() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }

        private void decrement() {
            lock.lock();
            try {
                count--;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        private void increment() {
            lock.lock();
            try {
                count++;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        @SuppressWarnings("BlockingMethodInNonBlockingContext")
        private void waitUntil(IntPredicate predicate) throws InterruptedException {
            lock.lock();
            try {
                while (!predicate.test(count)) {
                    condition.await();
                }
            } finally {
                lock.unlock();
            }
        }
    }
}