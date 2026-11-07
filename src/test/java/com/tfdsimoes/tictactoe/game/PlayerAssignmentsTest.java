package com.tfdsimoes.tictactoe.game;

import com.tfdsimoes.tictactoe.domain.Mark;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerAssignmentsTest {
    @Test
    void swapsPlayersBetweenMarksWhileXCanStartEachRound() {
        PlayerAssignments firstRound = PlayerAssignments.initial();
        PlayerAssignments secondRound = firstRound.nextRound();
        PlayerAssignments thirdRound = secondRound.nextRound();

        assertEquals(Mark.X, firstRound.markFor(PlayerId.FIRST));
        assertEquals(Mark.O, firstRound.markFor(PlayerId.SECOND));
        assertEquals(PlayerId.FIRST, firstRound.playerFor(Mark.X));

        assertEquals(Mark.O, secondRound.markFor(PlayerId.FIRST));
        assertEquals(Mark.X, secondRound.markFor(PlayerId.SECOND));
        assertEquals(PlayerId.FIRST, secondRound.playerFor(Mark.O));
        assertEquals(PlayerId.SECOND, secondRound.playerFor(Mark.X));
        assertTrue(GameMode.PLAYER_VS_COMPUTER.isHumanTurn(secondRound.playerFor(Mark.O)));
        assertFalse(GameMode.PLAYER_VS_COMPUTER.isHumanTurn(secondRound.playerFor(Mark.X)));

        assertEquals(firstRound.markFor(PlayerId.FIRST), thirdRound.markFor(PlayerId.FIRST));
    }
}
