package com.tfdsimoes.tictactoe;

import com.tfdsimoes.tictactoe.ai.MinimaxStrategy;
import com.tfdsimoes.tictactoe.game.Scoreboard;
import com.tfdsimoes.tictactoe.ui.GameFrame;
import javax.swing.SwingUtilities;

public final class TicTacToeApp {
    private TicTacToeApp() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameFrame(new Scoreboard(), new MinimaxStrategy()).setVisible(true);
        });
    }
}
