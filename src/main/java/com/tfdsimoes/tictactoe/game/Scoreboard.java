package com.tfdsimoes.tictactoe.game;

import com.tfdsimoes.tictactoe.domain.GameStatus;
import com.tfdsimoes.tictactoe.domain.Mark;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class Scoreboard {
    private final Map<Mark, Integer> wins = new EnumMap<>(Mark.class);
    private int draws;

    public Scoreboard() {
        for (Mark mark : Mark.values()) {
            wins.put(mark, 0);
        }
    }

    public void record(GameStatus result) {
        Objects.requireNonNull(result, "result");
        if (!result.isFinished()) {
            throw new IllegalArgumentException("An unfinished game has no score to record.");
        }
        if (result == GameStatus.DRAW) {
            draws++;
        } else {
            Mark winner = result.winner().orElseThrow();
            wins.put(winner, wins.get(winner) + 1);
        }
    }

    public int getWins(Mark mark) {
        return wins.get(Objects.requireNonNull(mark, "mark"));
    }

    public int getDraws() {
        return draws;
    }
}
