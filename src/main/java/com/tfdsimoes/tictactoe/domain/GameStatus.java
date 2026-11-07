package com.tfdsimoes.tictactoe.domain;

import java.util.Optional;

public enum GameStatus {
    IN_PROGRESS,
    DRAW,
    X_WON,
    O_WON;

    public boolean isFinished() {
        return this != IN_PROGRESS;
    }

    public Optional<Mark> winner() {
        return switch (this) {
            case X_WON -> Optional.of(Mark.X);
            case O_WON -> Optional.of(Mark.O);
            default -> Optional.empty();
        };
    }
}
