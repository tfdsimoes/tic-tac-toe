package com.tfdsimoes.tictactoe.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tfdsimoes.tictactoe.ai.MinimaxStrategy;
import com.tfdsimoes.tictactoe.domain.GameStatus;
import com.tfdsimoes.tictactoe.domain.Mark;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

class GameSessionTest {
    @Test
    void recordsWinsAndPersistsMarkRotationWhenACompletedModeIsReentered() {
        Scoreboard scoreboard = new Scoreboard();
        Map<GameMode, PlayerAssignments> assignments = new EnumMap<>(GameMode.class);
        assignments.put(GameMode.PLAYER_VS_PLAYER, PlayerAssignments.initial());
        ManualGameScheduler scheduler = new ManualGameScheduler();
        List<GameStatus> results = new ArrayList<>();

        GameSession firstSession = newSession(
                GameMode.PLAYER_VS_PLAYER,
                scoreboard,
                assignments.get(GameMode.PLAYER_VS_PLAYER),
                scheduler,
                updated -> assignments.put(GameMode.PLAYER_VS_PLAYER, updated),
                results,
                board -> 0);
        firstSession.start();
        playMoves(firstSession, 0, 3, 1, 4, 2);

        assertEquals(1, scoreboard.getWins(GameMode.PLAYER_VS_PLAYER, PlayerId.FIRST));
        assertEquals(List.of(GameStatus.X_WON), results);
        assertEquals(GameSession.ROUND_RESTART_DELAY_MS, scheduler.nextDelayMillis());
        scheduler.runNext();
        assertEquals(Mark.O, assignments.get(GameMode.PLAYER_VS_PLAYER).markFor(PlayerId.FIRST));
        assertEquals(Mark.X, firstSession.getBoard().getNextMark());

        GameSession reenteredSession = newSession(
                GameMode.PLAYER_VS_PLAYER,
                scoreboard,
                assignments.get(GameMode.PLAYER_VS_PLAYER),
                new ManualGameScheduler(),
                updated -> assignments.put(GameMode.PLAYER_VS_PLAYER, updated),
                new ArrayList<>(),
                board -> 0);
        assertEquals(Mark.O, reenteredSession.getPlayerAssignments().markFor(PlayerId.FIRST));
        assertEquals(1, scoreboard.getWins(GameMode.PLAYER_VS_PLAYER, PlayerId.FIRST));

        playMoves(firstSession, 0, 3, 1, 4, 2);
        assertEquals(1, scoreboard.getWins(GameMode.PLAYER_VS_PLAYER, PlayerId.SECOND));
    }

    @Test
    void swapsMarksAfterDrawAndKeepsXAsTheNextStartingMark() {
        Scoreboard scoreboard = new Scoreboard();
        ManualGameScheduler scheduler = new ManualGameScheduler();
        List<GameStatus> results = new ArrayList<>();
        GameSession session = newSession(
                GameMode.PLAYER_VS_PLAYER,
                scoreboard,
                PlayerAssignments.initial(),
                scheduler,
                ignored -> {},
                results,
                board -> 0);
        session.start();

        playMoves(session, 0, 1, 2, 4, 3, 5, 7, 6, 8);

        assertEquals(1, scoreboard.getDraws(GameMode.PLAYER_VS_PLAYER));
        assertEquals(List.of(GameStatus.DRAW), results);
        scheduler.runNext();
        assertEquals(Mark.X, session.getBoard().getNextMark());
        assertEquals(Mark.O, session.getPlayerAssignments().markFor(PlayerId.FIRST));
        assertEquals(PlayerId.SECOND, session.getPlayerAssignments().playerFor(Mark.X));
    }

    @Test
    void rotatesParticipantsAfterRoundsInEveryGameMode() {
        for (GameMode mode : GameMode.values()) {
            ManualGameScheduler scheduler = new ManualGameScheduler();
            GameSession session = newSession(
                    mode,
                    new Scoreboard(),
                    PlayerAssignments.initial(),
                    scheduler,
                    ignored -> {},
                    new ArrayList<>(),
                    board -> board.getLegalMoves().get(0));
            session.start();

            int moves = 0;
            while (!session.getBoard().getStatus().isFinished() && moves < 9) {
                PlayerId currentPlayer = session.getPlayerAssignments()
                        .playerFor(session.getBoard().getNextMark());
                if (mode.isHumanTurn(currentPlayer)) {
                    session.playHumanMove(session.getBoard().getLegalMoves().get(0));
                } else {
                    scheduler.runNext();
                }
                moves++;
            }

            assertTrue(session.getBoard().getStatus().isFinished(), mode.name());
            assertEquals(GameSession.ROUND_RESTART_DELAY_MS, scheduler.nextDelayMillis());
            scheduler.runNext();
            assertEquals(Mark.O, session.getPlayerAssignments().markFor(PlayerId.FIRST), mode.name());
            assertEquals(Mark.X, session.getBoard().getNextMark(), mode.name());
        }
    }

    @Test
    void delaysComputerMovesByOneSecond() {
        ManualGameScheduler scheduler = new ManualGameScheduler();
        GameSession session = newSession(
                GameMode.PLAYER_VS_COMPUTER,
                new Scoreboard(),
                PlayerAssignments.initial(),
                scheduler,
                ignored -> {},
                new ArrayList<>(),
                new MinimaxStrategy(new Random(1)));
        session.start();

        assertFalse(scheduler.hasPendingTask());
        session.playHumanMove(0);

        session.playHumanMove(1);
        assertNull(session.getBoard().getCell(1));
        assertEquals(GameSession.COMPUTER_MOVE_DELAY_MS, scheduler.nextDelayMillis());
        scheduler.runNext();
        assertEquals(
                2L,
                session.getBoard().getCells().stream()
                        .filter(mark -> mark != null)
                        .count());
    }

    @Test
    void delaysTheComputerOpeningMoveWhenItReceivesXInTheNextRound() {
        Scoreboard scoreboard = new Scoreboard();
        ManualGameScheduler scheduler = new ManualGameScheduler();
        PlayerAssignments[] assignments = {PlayerAssignments.initial()};
        int[] strategyMoves = {3, 4, 0};
        int[] strategyMoveIndex = {0};
        GameSession session = newSession(
                GameMode.PLAYER_VS_COMPUTER,
                scoreboard,
                assignments[0],
                scheduler,
                updated -> assignments[0] = updated,
                new ArrayList<>(),
                board -> strategyMoves[strategyMoveIndex[0]++]);
        session.start();

        playMoves(session, 0);
        assertEquals(GameSession.COMPUTER_MOVE_DELAY_MS, scheduler.nextDelayMillis());
        scheduler.runNext();
        playMoves(session, 1);
        scheduler.runNext();
        playMoves(session, 2);

        assertEquals(GameStatus.X_WON, session.getBoard().getStatus());
        assertEquals(1, scoreboard.getWins(GameMode.PLAYER_VS_COMPUTER, PlayerId.FIRST));
        assertEquals(GameSession.ROUND_RESTART_DELAY_MS, scheduler.nextDelayMillis());
        scheduler.runNext();

        assertEquals(Mark.X, session.getBoard().getNextMark());
        assertEquals(PlayerId.SECOND, session.getPlayerAssignments().playerFor(Mark.X));
        assertEquals(GameSession.COMPUTER_MOVE_DELAY_MS, scheduler.nextDelayMillis());
        session.playHumanMove(5);
        assertNull(session.getBoard().getCell(5));

        scheduler.runNext();

        assertEquals(Mark.X, session.getBoard().getCell(0));
        assertEquals(
                1L,
                session.getBoard().getCells().stream()
                        .filter(mark -> mark != null)
                        .count());
    }

    @Test
    void reportsAnIllegalStrategyMoveWithoutCorruptingTheBoard() {
        ManualGameScheduler scheduler = new ManualGameScheduler();
        List<String> errors = new ArrayList<>();
        GameSession session = new GameSession(
                GameMode.COMPUTER_VS_COMPUTER,
                new Scoreboard(),
                board -> 99,
                PlayerAssignments.initial(),
                scheduler,
                ignored -> {},
                new GameSession.Listener() {
                    @Override
                    public void onStateChanged() {}

                    @Override
                    public void onRoundFinished(GameStatus result) {}

                    @Override
                    public void onError(String message) {
                        errors.add(message);
                    }
                });
        session.start();

        assertEquals(GameSession.COMPUTER_MOVE_DELAY_MS, scheduler.nextDelayMillis());
        scheduler.runNext();

        assertEquals(9, session.getBoard().getLegalMoves().size());
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("legal move"));
        assertFalse(scheduler.hasPendingTask());
    }

    @Test
    void reportsExpectedStrategyExceptionsWithoutCorruptingTheBoard() {
        ManualGameScheduler scheduler = new ManualGameScheduler();
        List<String> errors = new ArrayList<>();
        GameSession session = new GameSession(
                GameMode.COMPUTER_VS_COMPUTER,
                new Scoreboard(),
                board -> {
                    throw new IllegalArgumentException("Strategy rejected the board.");
                },
                PlayerAssignments.initial(),
                scheduler,
                ignored -> {},
                new GameSession.Listener() {
                    @Override
                    public void onStateChanged() {}

                    @Override
                    public void onRoundFinished(GameStatus result) {}

                    @Override
                    public void onError(String message) {
                        errors.add(message);
                    }
                });
        session.start();

        scheduler.runNext();

        assertEquals(9, session.getBoard().getLegalMoves().size());
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("could not choose a legal move"));
        assertFalse(scheduler.hasPendingTask());
    }

    @Test
    void startingAComputerSessionTwiceDoesNotScheduleDuplicateMoves() {
        ManualGameScheduler scheduler = new ManualGameScheduler();
        GameSession session = newSession(
                GameMode.COMPUTER_VS_COMPUTER,
                new Scoreboard(),
                PlayerAssignments.initial(),
                scheduler,
                ignored -> {},
                new ArrayList<>(),
                board -> 0);

        session.start();
        session.start();

        assertEquals(GameSession.COMPUTER_MOVE_DELAY_MS, scheduler.nextDelayMillis());
        scheduler.runNext();
        assertEquals(8, session.getBoard().getLegalMoves().size());
        assertTrue(scheduler.hasPendingTask());
    }

    @Test
    void stoppingASessionCancelsPendingComputerAndRoundTasks() {
        ManualGameScheduler scheduler = new ManualGameScheduler();
        GameSession session = newSession(
                GameMode.PLAYER_VS_COMPUTER,
                new Scoreboard(),
                PlayerAssignments.initial(),
                scheduler,
                ignored -> {},
                new ArrayList<>(),
                new MinimaxStrategy(new Random(1)));
        session.start();
        session.playHumanMove(0);
        assertTrue(scheduler.hasPendingTask());

        session.stop();

        assertFalse(session.isActive());
        assertFalse(scheduler.hasPendingTask());
        assertNull(session.getBoard().getCell(1));
    }

    @Test
    void stoppingASessionCancelsPendingRoundRestart() {
        ManualGameScheduler scheduler = new ManualGameScheduler();
        GameSession session = newSession(
                GameMode.PLAYER_VS_PLAYER,
                new Scoreboard(),
                PlayerAssignments.initial(),
                scheduler,
                ignored -> {},
                new ArrayList<>(),
                board -> 0);
        session.start();
        playMoves(session, 0, 3, 1, 4, 2);

        assertEquals(GameSession.ROUND_RESTART_DELAY_MS, scheduler.nextDelayMillis());
        session.stop();

        assertFalse(scheduler.hasPendingTask());
        assertEquals(GameStatus.X_WON, session.getBoard().getStatus());
        assertNull(session.getBoard().getCell(5));
        session.playHumanMove(5);
        assertNull(session.getBoard().getCell(5));
    }

    @Test
    void stoppingIsIdempotentAndStoppedSessionsCannotRestartOrAcceptMoves() {
        ManualGameScheduler scheduler = new ManualGameScheduler();
        GameSession session = newSession(
                GameMode.PLAYER_VS_PLAYER,
                new Scoreboard(),
                PlayerAssignments.initial(),
                scheduler,
                ignored -> {},
                new ArrayList<>(),
                board -> 0);
        session.start();
        session.stop();

        session.stop();
        session.playHumanMove(0);

        assertFalse(session.isActive());
        assertNull(session.getBoard().getCell(0));
        assertThrows(IllegalStateException.class, session::start);
    }

    private GameSession newSession(
            GameMode mode,
            Scoreboard scoreboard,
            PlayerAssignments assignments,
            ManualGameScheduler scheduler,
            java.util.function.Consumer<PlayerAssignments> assignmentsChanged,
            List<GameStatus> results,
            com.tfdsimoes.tictactoe.ai.MoveStrategy strategy) {
        return new GameSession(
                mode, scoreboard, strategy, assignments, scheduler, assignmentsChanged, new GameSession.Listener() {
                    @Override
                    public void onStateChanged() {}

                    @Override
                    public void onRoundFinished(GameStatus result) {
                        results.add(result);
                    }

                    @Override
                    public void onError(String message) {}
                });
    }

    private void playMoves(GameSession session, int... moves) {
        for (int move : moves) {
            session.playHumanMove(move);
        }
    }
}
