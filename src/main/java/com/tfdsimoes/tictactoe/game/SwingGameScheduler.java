package com.tfdsimoes.tictactoe.game;

import java.util.Objects;
import javax.swing.Timer;

public final class SwingGameScheduler implements GameScheduler {
    @Override
    public Cancellable schedule(int delayMillis, Runnable task) {
        if (delayMillis < 0) {
            throw new IllegalArgumentException("Delay must not be negative.");
        }
        Objects.requireNonNull(task, "task");
        Timer timer = new Timer(delayMillis, event -> task.run());
        timer.setRepeats(false);
        timer.start();
        return timer::stop;
    }
}
