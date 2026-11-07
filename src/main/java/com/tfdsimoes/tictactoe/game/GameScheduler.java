package com.tfdsimoes.tictactoe.game;

public interface GameScheduler {
    Cancellable schedule(int delayMillis, Runnable task);

    interface Cancellable {
        void cancel();
    }
}
