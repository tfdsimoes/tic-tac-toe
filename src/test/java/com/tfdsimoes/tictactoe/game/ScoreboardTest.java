package com.tfdsimoes.tictactoe.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreboardTest {
    @Test
    void tracksWinsAndDrawsByParticipantAndMode() {
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.recordWin(GameMode.PLAYER_VS_PLAYER, PlayerId.FIRST);
        scoreboard.recordWin(GameMode.PLAYER_VS_PLAYER, PlayerId.SECOND);
        scoreboard.recordDraw(GameMode.PLAYER_VS_PLAYER);
        scoreboard.recordWin(GameMode.PLAYER_VS_COMPUTER, PlayerId.FIRST);

        assertEquals(1, scoreboard.getWins(GameMode.PLAYER_VS_PLAYER, PlayerId.FIRST));
        assertEquals(1, scoreboard.getWins(GameMode.PLAYER_VS_PLAYER, PlayerId.SECOND));
        assertEquals(1, scoreboard.getDraws(GameMode.PLAYER_VS_PLAYER));
        assertEquals(1, scoreboard.getWins(GameMode.PLAYER_VS_COMPUTER, PlayerId.FIRST));
        assertEquals(0, scoreboard.getWins(GameMode.PLAYER_VS_COMPUTER, PlayerId.SECOND));
        assertEquals(0, scoreboard.getDraws(GameMode.PLAYER_VS_COMPUTER));
    }
}
