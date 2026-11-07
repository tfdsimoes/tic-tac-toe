package com.tfdsimoes.tictactoe.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tfdsimoes.tictactoe.domain.Mark;
import com.tfdsimoes.tictactoe.game.GameMode;
import com.tfdsimoes.tictactoe.game.GameScheduler;
import com.tfdsimoes.tictactoe.game.PlayerAssignments;
import com.tfdsimoes.tictactoe.game.PlayerId;
import com.tfdsimoes.tictactoe.game.Scoreboard;
import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class GamePanelTest {
    @Test
    void menuButtonsExposeNamesAndSelectEachMode() throws Exception {
        List<GameMode> selectedModes = new ArrayList<>();
        AtomicReference<MainMenuPanel> panelReference = new AtomicReference<>();

        onEventDispatchThread(() -> panelReference.set(new MainMenuPanel(selectedModes::add)));
        onEventDispatchThread(() -> {
            MainMenuPanel panel = panelReference.get();
            for (GameMode mode : GameMode.values()) {
                JButton button = findButton(panel, mode.getDisplayName());
                assertEquals(
                        mode.getDisplayName(), button.getAccessibleContext().getAccessibleName());
                assertEquals(
                        mode.getDescription(), button.getAccessibleContext().getAccessibleDescription());
                button.doClick(0);
            }
        });

        assertEquals(List.of(GameMode.values()), selectedModes);
    }

    @Test
    void boardClicksUpdateAccessibleStateAndMenuReturnCancelsRoundTimer() throws Exception {
        TestGameScheduler scheduler = new TestGameScheduler();
        Scoreboard scoreboard = new Scoreboard();
        AtomicReference<PlayerAssignments> assignments = new AtomicReference<>(PlayerAssignments.initial());
        AtomicBoolean returnedToMenu = new AtomicBoolean();
        AtomicReference<GamePanel> panelReference = new AtomicReference<>();

        onEventDispatchThread(() -> panelReference.set(new GamePanel(
                GameMode.PLAYER_VS_PLAYER,
                scoreboard,
                board -> 0,
                assignments.get(),
                assignments::set,
                scheduler,
                () -> returnedToMenu.set(true))));

        onEventDispatchThread(() -> {
            GamePanel panel = panelReference.get();
            JButton firstCell = findButton(panel, "top-left cell");
            JLabel status = findLabel(panel, "Game status");
            JLabel firstScore = findLabel(panel, "Player 1 score");

            assertEquals("X to move - Player 1", status.getText());
            assertEquals("Game status", status.getAccessibleContext().getAccessibleName());
            firstCell.doClick(0);
            assertEquals("X", firstCell.getText());
            assertEquals("X game cell", firstCell.getAccessibleContext().getAccessibleDescription());
            assertEquals("O to move - Player 2", status.getText());
            assertTrue(
                    firstScore.getAccessibleContext().getAccessibleDescription().contains("0 wins"));

            findButton(panel, "middle-left cell").doClick(0);
            findButton(panel, "top-center cell").doClick(0);
            findButton(panel, "middle-center cell").doClick(0);
            findButton(panel, "top-right cell").doClick(0);

            assertTrue(status.getText().contains("X wins! (Player 1)"));
            assertTrue(status.getAccessibleContext().getAccessibleDescription().contains("Next round in 3 seconds"));
            assertEquals(1, scoreboard.getWins(GameMode.PLAYER_VS_PLAYER, PlayerId.FIRST));
            assertTrue(
                    firstScore.getAccessibleContext().getAccessibleDescription().contains("1 wins"));
            assertTrue(scheduler.hasPendingTask());
            assertEquals(3_000, scheduler.nextDelayMillis());

            findButton(panel, "Main menu").doClick(0);
        });

        assertTrue(returnedToMenu.get());
        assertFalse(scheduler.hasPendingTask());
        assertEquals(Mark.O, assignments.get().markFor(PlayerId.FIRST));
    }

    private static JButton findButton(Container container, String accessibleName) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton button
                    && accessibleName.equals(button.getAccessibleContext().getAccessibleName())) {
                return button;
            }
            if (component instanceof Container child) {
                JButton nestedButton = findButtonIfPresent(child, accessibleName);
                if (nestedButton != null) {
                    return nestedButton;
                }
            }
        }
        throw new AssertionError("Could not find button with accessible name: " + accessibleName);
    }

    private static JButton findButtonIfPresent(Container container, String accessibleName) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton button
                    && accessibleName.equals(button.getAccessibleContext().getAccessibleName())) {
                return button;
            }
            if (component instanceof Container child) {
                JButton nestedButton = findButtonIfPresent(child, accessibleName);
                if (nestedButton != null) {
                    return nestedButton;
                }
            }
        }
        return null;
    }

    private static JLabel findLabel(Container container, String accessibleName) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel label
                    && accessibleName.equals(label.getAccessibleContext().getAccessibleName())) {
                return label;
            }
            if (component instanceof Container child) {
                JLabel nestedLabel = findLabelIfPresent(child, accessibleName);
                if (nestedLabel != null) {
                    return nestedLabel;
                }
            }
        }
        throw new AssertionError("Could not find label with accessible name: " + accessibleName);
    }

    private static JLabel findLabelIfPresent(Container container, String accessibleName) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel label
                    && accessibleName.equals(label.getAccessibleContext().getAccessibleName())) {
                return label;
            }
            if (component instanceof Container child) {
                JLabel nestedLabel = findLabelIfPresent(child, accessibleName);
                if (nestedLabel != null) {
                    return nestedLabel;
                }
            }
        }
        return null;
    }

    private static void onEventDispatchThread(Runnable task) throws Exception {
        try {
            SwingUtilities.invokeAndWait(task);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof AssertionError assertionError) {
                throw assertionError;
            }
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new AssertionError("Swing event-thread task failed.", cause);
        }
    }

    private static final class TestGameScheduler implements GameScheduler {
        private final List<ScheduledTask> tasks = new ArrayList<>();

        @Override
        public Cancellable schedule(int delayMillis, Runnable task) {
            ScheduledTask scheduledTask = new ScheduledTask(delayMillis, task);
            tasks.add(scheduledTask);
            return scheduledTask;
        }

        private boolean hasPendingTask() {
            return tasks.stream().anyMatch(task -> !task.completed && !task.cancelled);
        }

        private int nextDelayMillis() {
            return tasks.stream()
                    .filter(task -> !task.completed && !task.cancelled)
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("No task is pending."))
                    .delayMillis;
        }

        private static final class ScheduledTask implements Cancellable {
            private final int delayMillis;
            private final Runnable runnable;
            private boolean completed;
            private boolean cancelled;

            private ScheduledTask(int delayMillis, Runnable runnable) {
                this.delayMillis = delayMillis;
                this.runnable = runnable;
            }

            @Override
            public void cancel() {
                cancelled = true;
            }
        }
    }
}
