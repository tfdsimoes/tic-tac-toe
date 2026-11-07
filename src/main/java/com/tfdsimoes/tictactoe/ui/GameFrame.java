package com.tfdsimoes.tictactoe.ui;

import com.tfdsimoes.tictactoe.ai.MoveStrategy;
import com.tfdsimoes.tictactoe.game.GameMode;
import com.tfdsimoes.tictactoe.game.PlayerAssignments;
import com.tfdsimoes.tictactoe.game.Scoreboard;
import com.tfdsimoes.tictactoe.game.SwingGameScheduler;
import java.awt.CardLayout;
import java.util.EnumMap;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JPanel;

public final class GameFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel screenContainer = new JPanel(cardLayout);
    private final Scoreboard scoreboard;
    private final MoveStrategy moveStrategy;
    private final EnumMap<GameMode, PlayerAssignments> assignments = new EnumMap<>(GameMode.class);

    public GameFrame(Scoreboard scoreboard, MoveStrategy moveStrategy) {
        super("Tic Tac Toe");
        this.scoreboard = Objects.requireNonNull(scoreboard, "scoreboard");
        this.moveStrategy = Objects.requireNonNull(moveStrategy, "moveStrategy");
        for (GameMode mode : GameMode.values()) {
            assignments.put(mode, PlayerAssignments.initial());
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(screenContainer);
        setMinimumSize(new java.awt.Dimension(560, 650));
        setSize(760, 820);
        setLocationRelativeTo(null);
        showMainMenu();
    }

    private void showMainMenu() {
        screenContainer.removeAll();
        screenContainer.add(new MainMenuPanel(this::startGame), "menu");
        cardLayout.show(screenContainer, "menu");
        screenContainer.revalidate();
        screenContainer.repaint();
    }

    private void startGame(GameMode mode) {
        screenContainer.removeAll();
        screenContainer.add(
                new GamePanel(
                        mode,
                        scoreboard,
                        moveStrategy,
                        assignments.get(mode),
                        updatedAssignments -> assignments.put(mode, updatedAssignments),
                        new SwingGameScheduler(),
                        this::showMainMenu),
                "game");
        cardLayout.show(screenContainer, "game");
        screenContainer.revalidate();
        screenContainer.repaint();
    }
}
