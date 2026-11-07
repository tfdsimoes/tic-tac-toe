package com.tfdsimoes.tictactoe.ai;

import com.tfdsimoes.tictactoe.domain.GameBoard;

@FunctionalInterface
public interface MoveStrategy {
    int chooseMove(GameBoard board);
}
