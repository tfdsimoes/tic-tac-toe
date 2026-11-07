package com.tfdsimoes.tictactoe.ui;

import com.tfdsimoes.tictactoe.ai.MoveStrategy;
import com.tfdsimoes.tictactoe.game.GameMode;
import com.tfdsimoes.tictactoe.game.Scoreboard;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.util.Objects;

public final class GameFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel screenContainer = new JPanel(cardLayout);
    private final Scoreboard scoreboard;
    private final MoveStrategy moveStrategy;

    public GameFrame(Scoreboard scoreboard, MoveStrategy moveStrategy) {
        super("Tic Tac Toe");
        this.scoreboard = Objects.requireNonNull(scoreboard, "scoreboard");
        this.moveStrategy = Objects.requireNonNull(moveStrategy, "moveStrategy");

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
        screenContainer.add(new GamePanel(mode, scoreboard, moveStrategy, this::showMainMenu), "game");
        cardLayout.show(screenContainer, "game");
        screenContainer.revalidate();
        screenContainer.repaint();
    }
}
