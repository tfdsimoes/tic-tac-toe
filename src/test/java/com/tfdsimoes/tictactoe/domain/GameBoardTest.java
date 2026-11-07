package com.tfdsimoes.tictactoe.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameBoardTest {
    @Test
    void alternatesMarksAndDetectsWinner() {
        GameBoard board = GameBoard.newGame(Mark.X)
                .play(0)
                .play(3)
                .play(1)
                .play(4)
                .play(2);

        assertEquals(GameStatus.X_WON, board.getStatus());
        assertEquals(Mark.O, board.getNextMark());
        assertEquals(Mark.X, board.getCell(2));
    }

    @Test
    void detectsDraw() {
        GameBoard board = GameBoard.newGame(Mark.X)
                .play(0).play(1).play(2)
                .play(4).play(3).play(5)
                .play(7).play(6).play(8);

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
                List.of(2, 0, 4, 1, 6)
        );

        for (List<Integer> sequence : winningSequences) {
            GameBoard board = GameBoard.newGame(Mark.X);
            for (int move : sequence) {
                board = board.play(move);
            }
            assertEquals(GameStatus.X_WON, board.getStatus(), "Winning sequence: " + sequence);
        }
    }

    @Test
    void rejectsOccupiedOrOutOfRangeCells() {
        GameBoard board = GameBoard.newGame(Mark.X).play(0);

        assertThrows(IllegalArgumentException.class, () -> board.play(0));
        assertThrows(IllegalArgumentException.class, () -> board.play(9));
        assertThrows(IllegalArgumentException.class, () -> board.play(-1));
    }

    @Test
    void rejectsMovesAfterTheGameEnds() {
        GameBoard board = GameBoard.newGame(Mark.X)
                .play(0).play(3).play(1).play(4).play(2);

        assertThrows(IllegalStateException.class, () -> board.play(5));
    }
}
