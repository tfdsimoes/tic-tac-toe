package com.tfdsimoes.tictactoe.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class GameBoardTest {
    @Test
    void alternatesMarksAndDetectsWinner() {
        GameBoard board =
                GameBoard.newGame(Mark.X).play(0).play(3).play(1).play(4).play(2);

        assertEquals(GameStatus.X_WON, board.getStatus());
        assertEquals(Mark.O, board.getNextMark());
        assertEquals(Mark.X, board.getCell(2));
    }

    @Test
    void detectsDraw() {
        GameBoard board = GameBoard.newGame(Mark.X)
                .play(0)
                .play(1)
                .play(2)
                .play(4)
                .play(3)
                .play(5)
                .play(7)
                .play(6)
                .play(8);

        assertEquals(GameStatus.DRAW, board.getStatus());
        assertEquals(0, board.getLegalMoves().size());
    }

    @Test
    void detectsWinsAcrossEveryRowColumnAndDiagonal() {
        List<List<Integer>> winningSequences = List.of(
                List.of(0, 3, 1, 4, 2),
                List.of(3, 0, 4, 1, 5),
                List.of(6, 0, 7, 1, 8),
                List.of(0, 1, 3, 2, 6),
                List.of(1, 0, 4, 2, 7),
                List.of(2, 0, 5, 1, 8),
                List.of(0, 1, 4, 2, 8),
                List.of(2, 0, 4, 1, 6));

        for (List<Integer> sequence : winningSequences) {
            GameBoard board = GameBoard.newGame(Mark.X);
            for (int move : sequence) {
                board = board.play(move);
            }
            assertEquals(GameStatus.X_WON, board.getStatus(), "Winning sequence: " + sequence);
        }
    }

    @Test
    void detectsOWinsAcrossEveryRowColumnAndDiagonal() {
        List<List<Integer>> winningSequences = List.of(
                List.of(3, 0, 4, 1, 8, 2),
                List.of(0, 3, 1, 4, 8, 5),
                List.of(0, 6, 1, 7, 3, 8),
                List.of(1, 0, 8, 3, 5, 6),
                List.of(0, 1, 8, 4, 3, 7),
                List.of(0, 2, 1, 5, 3, 8),
                List.of(0, 1, 2, 4, 8, 7),
                List.of(0, 2, 1, 4, 3, 6));

        for (List<Integer> sequence : winningSequences) {
            GameBoard board = GameBoard.newGame(Mark.X);
            for (int move : sequence) {
                board = board.play(move);
            }
            assertEquals(GameStatus.O_WON, board.getStatus(), "Winning sequence: " + sequence);
        }
    }

    @Test
    void rejectsOccupiedOrOutOfRangeCells() {
        GameBoard board = GameBoard.newGame(Mark.X).play(0);

        assertThrows(IllegalArgumentException.class, () -> board.play(0));
        assertThrows(IllegalArgumentException.class, () -> board.play(9));
        assertThrows(IllegalArgumentException.class, () -> board.play(-1));
        assertThrows(IllegalArgumentException.class, () -> board.getCell(9));
        assertThrows(IllegalArgumentException.class, () -> board.getCell(-1));
    }

    @Test
    void rejectsNullStartingMark() {
        assertThrows(NullPointerException.class, () -> GameBoard.newGame(null));
    }

    @Test
    void isImmutableAndReturnsUnmodifiableCollections() {
        GameBoard emptyBoard = GameBoard.newGame(Mark.X);
        GameBoard playedBoard = emptyBoard.play(0);

        assertNull(emptyBoard.getCell(0));
        assertEquals(Mark.X, playedBoard.getCell(0));
        assertThrows(
                UnsupportedOperationException.class, () -> emptyBoard.getCells().set(0, Mark.O));
        assertThrows(
                UnsupportedOperationException.class,
                () -> emptyBoard.getLegalMoves().add(9));
        assertTrue(emptyBoard.getLegalMoves().contains(0));
    }

    @Test
    void rejectsMovesAfterTheGameEnds() {
        GameBoard board =
                GameBoard.newGame(Mark.X).play(0).play(3).play(1).play(4).play(2);

        assertThrows(IllegalStateException.class, () -> board.play(5));
    }
}
