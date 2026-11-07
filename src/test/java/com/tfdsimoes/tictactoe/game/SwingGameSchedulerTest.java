package com.tfdsimoes.tictactoe.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class SwingGameSchedulerTest {
    private final SwingGameScheduler scheduler = new SwingGameScheduler();

    @Test
    void runsScheduledTaskOnTheSwingEventDispatchThread() throws InterruptedException {
        CountDownLatch taskRan = new CountDownLatch(1);
        AtomicReference<Boolean> ranOnEventDispatchThread = new AtomicReference<>(false);
        AtomicInteger callCount = new AtomicInteger();

        scheduler.schedule(0, () -> {
            ranOnEventDispatchThread.set(SwingUtilities.isEventDispatchThread());
            callCount.incrementAndGet();
            taskRan.countDown();
        });

        assertTrue(taskRan.await(2, TimeUnit.SECONDS));
        flushEventQueue();

        assertTrue(ranOnEventDispatchThread.get());
        assertEquals(1, callCount.get());
    }

    @Test
    void cancellationPreventsScheduledTaskFromRunning() throws InterruptedException {
        AtomicInteger callCount = new AtomicInteger();
        AtomicReference<GameScheduler.Cancellable> cancellable = new AtomicReference<>();

        onEventDispatchThread(() -> cancellable.set(scheduler.schedule(1_000, callCount::incrementAndGet)));
        cancellable.get().cancel();
        flushEventQueue();

        assertEquals(0, callCount.get());
    }

    @Test
    void rejectsNegativeDelayAndNullTask() {
        assertThrows(IllegalArgumentException.class, () -> scheduler.schedule(-1, () -> {}));
        assertThrows(NullPointerException.class, () -> scheduler.schedule(0, null));
    }

    private void flushEventQueue() {
        onEventDispatchThread(() -> {});
    }

    private void onEventDispatchThread(Runnable task) {
        try {
            SwingUtilities.invokeAndWait(task);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting for the Swing event thread.", exception);
        } catch (InvocationTargetException exception) {
            throw new AssertionError("Swing event-thread task failed.", exception.getCause());
        }
    }
}
