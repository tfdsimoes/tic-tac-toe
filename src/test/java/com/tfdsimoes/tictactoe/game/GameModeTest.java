package com.tfdsimoes.tictactoe.game;

import com.tfdsimoes.tictactoe.domain.Mark;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameModeTest {
    @Test
    void assignsHumanAndComputerPlayersByMode() {
        assertTrue(GameMode.PLAYER_VS_PLAYER.isHumanTurn(Mark.X));
        assertTrue(GameMode.PLAYER_VS_PLAYER.isHumanTurn(Mark.O));
        assertTrue(GameMode.PLAYER_VS_COMPUTER.isHumanTurn(Mark.X));
        assertFalse(GameMode.PLAYER_VS_COMPUTER.isHumanTurn(Mark.O));
        assertFalse(GameMode.COMPUTER_VS_COMPUTER.isHumanTurn(Mark.X));
        assertFalse(GameMode.COMPUTER_VS_COMPUTER.isHumanTurn(Mark.O));
    }

    @Test
    void givesEachSideADistinctScoreboardName() {
        assertEquals("Player 1", GameMode.PLAYER_VS_PLAYER.getPlayerName(Mark.X));
        assertEquals("Player 2", GameMode.PLAYER_VS_PLAYER.getPlayerName(Mark.O));
        assertEquals("Machine 1", GameMode.COMPUTER_VS_COMPUTER.getPlayerName(Mark.X));
        assertEquals("Machine 2", GameMode.COMPUTER_VS_COMPUTER.getPlayerName(Mark.O));
    }
}
