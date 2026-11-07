package com.tfdsimoes.tictactoe.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tfdsimoes.tictactoe.domain.GameBoard;
import com.tfdsimoes.tictactoe.domain.GameStatus;
import com.tfdsimoes.tictactoe.domain.Mark;
import java.util.Random;
import org.junit.jupiter.api.Test;

class MinimaxStrategyTest {
    private final MinimaxStrategy strategy = new MinimaxStrategy(new Random(7));

    @Test
    void takesAnImmediateWinningMove() {
        GameBoard board = GameBoard.newGame(Mark.X).play(0).play(3).play(1).play(4);

        assertEquals(2, strategy.chooseMove(board));
    }

    @Test
    void blocksAnImmediateOpponentWin() {
        GameBoard board = GameBoard.newGame(Mark.X).play(0).play(3).play(1);

        assertEquals(2, strategy.chooseMove(board));
    }

    @Test
    void alwaysChoosesAnAvailableCell() {
        GameBoard board = GameBoard.newGame(Mark.O).play(4);

        int move = strategy.chooseMove(board);

        assertTrue(board.getLegalMoves().contains(move));
    }

    @Test
    void takesAnImmediateWinningMoveAsEitherMark() {
        GameBoard board = GameBoard.newGame(Mark.O).play(0).play(3).play(1).play(4);

        assertEquals(2, strategy.chooseMove(board));
    }

    @Test
    void blocksAnImmediateOpponentWinAsEitherMark() {
        GameBoard board = GameBoard.newGame(Mark.O).play(0).play(3).play(1);

        assertEquals(2, strategy.chooseMove(board));
    }

    @Test
    void rejectsNullOrFinishedBoards() {
        GameBoard finishedBoard =
                GameBoard.newGame(Mark.X).play(0).play(3).play(1).play(4).play(2);

        assertThrows(NullPointerException.class, () -> strategy.chooseMove(null));
        assertThrows(IllegalArgumentException.class, () -> strategy.chooseMove(finishedBoard));
    }

    @Test
    void cannotBeForcedToLoseFromEitherStartingMark() {
        assertCannotLose(GameBoard.newGame(Mark.X), Mark.X);
        assertCannotLose(GameBoard.newGame(Mark.O), Mark.O);
    }

    private void assertCannotLose(GameBoard board, Mark computerMark) {
        if (board.getStatus().isFinished()) {
            assertNotEquals(computerMark.opposite(), board.getStatus().winner().orElse(null));
            return;
        }

        if (board.getNextMark() == computerMark) {
            assertCannotLose(board.play(strategy.chooseMove(board)), computerMark);
            return;
        }

        for (int move : board.getLegalMoves()) {
            GameBoard nextBoard = board.play(move);
            if (nextBoard.getStatus() == GameStatus.X_WON || nextBoard.getStatus() == GameStatus.O_WON) {
                assertNotEquals(
                        computerMark.opposite(), nextBoard.getStatus().winner().orElse(null));
            } else {
                assertCannotLose(nextBoard, computerMark);
            }
        }
    }
}
