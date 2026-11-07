package com.tfdsimoes.tictactoe.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class ManualGameScheduler implements GameScheduler {
    private final List<ScheduledTask> tasks = new ArrayList<>();

    @Override
    public Cancellable schedule(int delayMillis, Runnable task) {
        ScheduledTask scheduledTask = new ScheduledTask(delayMillis, Objects.requireNonNull(task, "task"));
        tasks.add(scheduledTask);
        return scheduledTask;
    }

    int nextDelayMillis() {
        return nextTask().delayMillis;
    }

    void runNext() {
        ScheduledTask task = nextTask();
        task.completed = true;
        task.runnable.run();
    }

    boolean hasPendingTask() {
        return tasks.stream().anyMatch(task -> !task.completed && !task.cancelled);
    }

    private ScheduledTask nextTask() {
        return tasks.stream()
                .filter(task -> !task.completed && !task.cancelled)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No scheduled task is pending."));
    }

    private static final class ScheduledTask implements Cancellable {
        private final int delayMillis;
        private final Runnable runnable;
        private boolean completed;
        private boolean cancelled;

        private ScheduledTask(int delayMillis, Runnable runnable) {
            this.delayMillis = delayMillis;
            this.runnable = runnable;
        }

        @Override
        public void cancel() {
            cancelled = true;
        }
    }
}
