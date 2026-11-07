package com.tfdsimoes.tictactoe.game;

import com.tfdsimoes.tictactoe.domain.GameStatus;
import com.tfdsimoes.tictactoe.domain.Mark;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScoreboardTest {
    @Test
    void tracksWinsAndDrawsSeparately() {
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.record(GameStatus.X_WON);
        scoreboard.record(GameStatus.O_WON);
        scoreboard.record(GameStatus.DRAW);

        assertEquals(1, scoreboard.getWins(Mark.X));
        assertEquals(1, scoreboard.getWins(Mark.O));
        assertEquals(1, scoreboard.getDraws());
    }

    @Test
    void doesNotRecordAnUnfinishedGame() {
        Scoreboard scoreboard = new Scoreboard();

        assertThrows(IllegalArgumentException.class, () -> scoreboard.record(GameStatus.IN_PROGRESS));
    }
}
