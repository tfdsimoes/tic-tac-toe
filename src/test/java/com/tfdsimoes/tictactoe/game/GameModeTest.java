package com.tfdsimoes.tictactoe.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameModeTest {
    @Test
    void assignsHumanAndComputerPlayersByMode() {
        assertTrue(GameMode.PLAYER_VS_PLAYER.isHumanTurn(PlayerId.FIRST));
        assertTrue(GameMode.PLAYER_VS_PLAYER.isHumanTurn(PlayerId.SECOND));
        assertTrue(GameMode.PLAYER_VS_COMPUTER.isHumanTurn(PlayerId.FIRST));
        assertFalse(GameMode.PLAYER_VS_COMPUTER.isHumanTurn(PlayerId.SECOND));
        assertFalse(GameMode.COMPUTER_VS_COMPUTER.isHumanTurn(PlayerId.FIRST));
        assertFalse(GameMode.COMPUTER_VS_COMPUTER.isHumanTurn(PlayerId.SECOND));
    }

    @Test
    void givesEachSideADistinctScoreboardName() {
        assertEquals("Player 1", GameMode.PLAYER_VS_PLAYER.getPlayerName(PlayerId.FIRST));
        assertEquals("Player 2", GameMode.PLAYER_VS_PLAYER.getPlayerName(PlayerId.SECOND));
        assertEquals("Machine 1", GameMode.COMPUTER_VS_COMPUTER.getPlayerName(PlayerId.FIRST));
        assertEquals("Machine 2", GameMode.COMPUTER_VS_COMPUTER.getPlayerName(PlayerId.SECOND));
    }
}
