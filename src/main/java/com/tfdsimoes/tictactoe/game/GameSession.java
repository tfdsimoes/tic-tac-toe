package com.tfdsimoes.tictactoe.game;

import com.tfdsimoes.tictactoe.ai.MoveStrategy;
import com.tfdsimoes.tictactoe.domain.GameBoard;
import com.tfdsimoes.tictactoe.domain.GameStatus;
import com.tfdsimoes.tictactoe.domain.Mark;
import java.util.Objects;
import java.util.function.Consumer;

public final class GameSession {
    public static final int COMPUTER_MOVE_DELAY_MS = 1_000;
    public static final int ROUND_RESTART_DELAY_MS = 3_000;

    private final GameMode mode;
    private final Scoreboard scoreboard;
    private final MoveStrategy moveStrategy;
    private final GameScheduler scheduler;
    private final Consumer<PlayerAssignments> assignmentsChanged;
    private final Listener listener;

    private PlayerAssignments playerAssignments;
    private GameBoard board = GameBoard.newGame(Mark.X);
    private GameScheduler.Cancellable pendingTask;
    private boolean active = true;
    private boolean started;
    private boolean strategyFailed;

    public GameSession(
            GameMode mode,
            Scoreboard scoreboard,
            MoveStrategy moveStrategy,
            PlayerAssignments playerAssignments,
            GameScheduler scheduler,
            Consumer<PlayerAssignments> assignmentsChanged,
            Listener listener) {
        this.mode = Objects.requireNonNull(mode, "mode");
        this.scoreboard = Objects.requireNonNull(scoreboard, "scoreboard");
        this.moveStrategy = Objects.requireNonNull(moveStrategy, "moveStrategy");
        this.playerAssignments = Objects.requireNonNull(playerAssignments, "playerAssignments");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.assignmentsChanged = Objects.requireNonNull(assignmentsChanged, "assignmentsChanged");
        this.listener = Objects.requireNonNull(listener, "listener");
    }

    public void start() {
        if (!active) {
            throw new IllegalStateException("A stopped game session cannot be started.");
        }
        if (started) {
            return;
        }
        started = true;
        listener.onStateChanged();
        scheduleComputerMoveIfNeeded();
    }

    public void playHumanMove(int index) {
        if (!active
                || strategyFailed
                || board.getStatus().isFinished()
                || !mode.isHumanTurn(playerForMark(board.getNextMark()))) {
            return;
        }
        applyMove(index);
    }

    public void stop() {
        active = false;
        cancelPendingTask();
    }

    public GameBoard getBoard() {
        return board;
    }

    public PlayerAssignments getPlayerAssignments() {
        return playerAssignments;
    }

    public boolean isActive() {
        return active;
    }

    private void applyMove(int index) {
        board = board.play(index);
        if (board.getStatus().isFinished()) {
            recordResult();
            listener.onStateChanged();
            listener.onRoundFinished(board.getStatus());
            pendingTask = scheduler.schedule(ROUND_RESTART_DELAY_MS, this::startNextRound);
            return;
        }
        listener.onStateChanged();
        scheduleComputerMoveIfNeeded();
    }

    private void recordResult() {
        GameStatus result = board.getStatus();
        if (result == GameStatus.DRAW) {
            scoreboard.recordDraw(mode);
        } else {
            scoreboard.recordWin(mode, playerForMark(result.winner().orElseThrow()));
        }
    }

    private void startNextRound() {
        pendingTask = null;
        if (!active) {
            return;
        }
        playerAssignments = playerAssignments.nextRound();
        assignmentsChanged.accept(playerAssignments);
        board = GameBoard.newGame(Mark.X);
        strategyFailed = false;
        listener.onStateChanged();
        scheduleComputerMoveIfNeeded();
    }

    private void scheduleComputerMoveIfNeeded() {
        if (!active
                || strategyFailed
                || board.getStatus().isFinished()
                || mode.isHumanTurn(playerForMark(board.getNextMark()))) {
            return;
        }
        pendingTask = scheduler.schedule(COMPUTER_MOVE_DELAY_MS, this::playComputerMove);
    }

    private void playComputerMove() {
        pendingTask = null;
        if (!active
                || strategyFailed
                || board.getStatus().isFinished()
                || mode.isHumanTurn(playerForMark(board.getNextMark()))) {
            return;
        }

        int move;
        try {
            move = moveStrategy.chooseMove(board);
        } catch (IllegalArgumentException exception) {
            failStrategy();
            return;
        }
        if (!board.getLegalMoves().contains(move)) {
            failStrategy();
            return;
        }
        applyMove(move);
    }

    private void failStrategy() {
        strategyFailed = true;
        listener.onError("The computer could not choose a legal move. Return to the main menu to restart.");
    }

    private void cancelPendingTask() {
        if (pendingTask != null) {
            pendingTask.cancel();
            pendingTask = null;
        }
    }

    private PlayerId playerForMark(Mark mark) {
        return playerAssignments.playerFor(mark);
    }

    public interface Listener {
        void onStateChanged();

        void onRoundFinished(GameStatus result);

        void onError(String message);
    }
}
