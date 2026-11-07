package com.tfdsimoes.tictactoe.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GameBoard {
    private static final int CELL_COUNT = 9;
    private static final int[][] WINNING_LINES = {
        {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
        {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
        {0, 4, 8}, {2, 4, 6}
    };

    private final Mark[] cells;
    private final Mark nextMark;
    private final GameStatus status;

    private GameBoard(Mark[] cells, Mark nextMark) {
        this.cells = cells.clone();
        this.nextMark = Objects.requireNonNull(nextMark, "nextMark");
        this.status = evaluateStatus(this.cells);
    }

    public static GameBoard newGame(Mark startingMark) {
        return new GameBoard(new Mark[CELL_COUNT], startingMark);
    }

    public GameBoard play(int index) {
        if (status.isFinished()) {
            throw new IllegalStateException("The game is already finished.");
        }
        if (index < 0 || index >= CELL_COUNT) {
            throw new IllegalArgumentException("Cell index must be between 0 and 8.");
        }
        if (cells[index] != null) {
            throw new IllegalArgumentException("That cell is already occupied.");
        }

        Mark[] updatedCells = cells.clone();
        updatedCells[index] = nextMark;
        return new GameBoard(updatedCells, nextMark.opposite());
    }

    public Mark getCell(int index) {
        if (index < 0 || index >= CELL_COUNT) {
            throw new IllegalArgumentException("Cell index must be between 0 and 8.");
        }
        return cells[index];
    }

    public List<Mark> getCells() {
        return Collections.unmodifiableList(Arrays.asList(cells.clone()));
    }

    public List<Integer> getLegalMoves() {
        List<Integer> moves = new ArrayList<>();
        if (!status.isFinished()) {
            for (int index = 0; index < CELL_COUNT; index++) {
                if (cells[index] == null) {
                    moves.add(index);
                }
            }
        }
        return Collections.unmodifiableList(moves);
    }

    public Mark getNextMark() {
        return nextMark;
    }

    public GameStatus getStatus() {
        return status;
    }

    private static GameStatus evaluateStatus(Mark[] cells) {
        for (int[] line : WINNING_LINES) {
            Mark first = cells[line[0]];
            if (first != null && first == cells[line[1]] && first == cells[line[2]]) {
                return first == Mark.X ? GameStatus.X_WON : GameStatus.O_WON;
            }
        }

        for (Mark cell : cells) {
            if (cell == null) {
                return GameStatus.IN_PROGRESS;
            }
        }
        return GameStatus.DRAW;
    }
}
