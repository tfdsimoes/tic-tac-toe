package com.tfdsimoes.tictactoe.ai;

import com.tfdsimoes.tictactoe.domain.GameBoard;

@FunctionalInterface
public interface MoveStrategy {
    /**
     * Chooses a legal move for the next mark on an in-progress board.
     *
     * @param board an in-progress game board
     * @return an index contained in {@link GameBoard#getLegalMoves()}
     */
    int chooseMove(GameBoard board);
}
