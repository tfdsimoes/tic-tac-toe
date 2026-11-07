package com.tfdsimoes.tictactoe.game;

/**
 * Schedules session callbacks.
 *
 * <p>Implementations must run callbacks serially on the session's owning thread. The Swing
 * implementation uses the event-dispatch thread.
 */
public interface GameScheduler {
    Cancellable schedule(int delayMillis, Runnable task);

    interface Cancellable {
        void cancel();
    }
}
