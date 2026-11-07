package com.tfdsimoes.tictactoe.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ScoreboardTest {
    @Test
    void tracksWinsAndDrawsByParticipantAndMode() {
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.recordWin(GameMode.PLAYER_VS_PLAYER, PlayerId.FIRST);
        scoreboard.recordWin(GameMode.PLAYER_VS_PLAYER, PlayerId.SECOND);
        scoreboard.recordDraw(GameMode.PLAYER_VS_PLAYER);
        scoreboard.recordWin(GameMode.PLAYER_VS_COMPUTER, PlayerId.FIRST);
        scoreboard.recordWin(GameMode.COMPUTER_VS_COMPUTER, PlayerId.SECOND);
        scoreboard.recordDraw(GameMode.COMPUTER_VS_COMPUTER);

        assertEquals(1, scoreboard.getWins(GameMode.PLAYER_VS_PLAYER, PlayerId.FIRST));
        assertEquals(1, scoreboard.getWins(GameMode.PLAYER_VS_PLAYER, PlayerId.SECOND));
        assertEquals(1, scoreboard.getDraws(GameMode.PLAYER_VS_PLAYER));
        assertEquals(1, scoreboard.getWins(GameMode.PLAYER_VS_COMPUTER, PlayerId.FIRST));
        assertEquals(0, scoreboard.getWins(GameMode.PLAYER_VS_COMPUTER, PlayerId.SECOND));
        assertEquals(0, scoreboard.getDraws(GameMode.PLAYER_VS_COMPUTER));
        assertEquals(0, scoreboard.getWins(GameMode.COMPUTER_VS_COMPUTER, PlayerId.FIRST));
        assertEquals(1, scoreboard.getWins(GameMode.COMPUTER_VS_COMPUTER, PlayerId.SECOND));
        assertEquals(1, scoreboard.getDraws(GameMode.COMPUTER_VS_COMPUTER));
    }

    @Test
    void rejectsNullModesAndPlayers() {
        Scoreboard scoreboard = new Scoreboard();
        assertThrows(NullPointerException.class, () -> scoreboard.recordWin(null, PlayerId.FIRST));
        assertThrows(NullPointerException.class, () -> scoreboard.recordWin(GameMode.PLAYER_VS_PLAYER, null));
        assertThrows(NullPointerException.class, () -> scoreboard.recordDraw(null));
        assertThrows(NullPointerException.class, () -> scoreboard.getWins(null, PlayerId.FIRST));
        assertThrows(NullPointerException.class, () -> scoreboard.getWins(GameMode.PLAYER_VS_PLAYER, null));
        assertThrows(NullPointerException.class, () -> scoreboard.getDraws(null));
    }
}
