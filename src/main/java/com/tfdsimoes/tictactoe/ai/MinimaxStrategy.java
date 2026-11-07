package com.tfdsimoes.tictactoe.ai;

import com.tfdsimoes.tictactoe.domain.GameBoard;
import com.tfdsimoes.tictactoe.domain.GameStatus;
import com.tfdsimoes.tictactoe.domain.Mark;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class MinimaxStrategy implements MoveStrategy {
    private final Random random;

    public MinimaxStrategy() {
        this(new Random());
    }

    public MinimaxStrategy(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    @Override
    public int chooseMove(GameBoard board) {
        Objects.requireNonNull(board, "board");
        if (board.getStatus().isFinished()) {
            throw new IllegalArgumentException("The computer cannot move after the game ends.");
        }

        Mark computerMark = board.getNextMark();
        int bestScore = Integer.MIN_VALUE;
        List<Integer> bestMoves = new ArrayList<>();

        for (int move : board.getLegalMoves()) {
            int score = evaluate(board.play(move), computerMark, 1);
            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }
        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    private int evaluate(GameBoard board, Mark computerMark, int depth) {
        GameStatus status = board.getStatus();
        if (status.isFinished()) {
            if (status == GameStatus.DRAW) {
                return 0;
            }
            return status.winner().orElseThrow() == computerMark ? 10 - depth : depth - 10;
        }

        boolean maximizing = board.getNextMark() == computerMark;
        int bestScore = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int move : board.getLegalMoves()) {
            int score = evaluate(board.play(move), computerMark, depth + 1);
            bestScore = maximizing ? Math.max(bestScore, score) : Math.min(bestScore, score);
        }
        return bestScore;
    }
}
