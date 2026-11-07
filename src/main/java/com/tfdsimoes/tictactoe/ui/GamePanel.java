package com.tfdsimoes.tictactoe.ui;

import com.tfdsimoes.tictactoe.ai.MoveStrategy;
import com.tfdsimoes.tictactoe.domain.GameBoard;
import com.tfdsimoes.tictactoe.domain.GameStatus;
import com.tfdsimoes.tictactoe.domain.Mark;
import com.tfdsimoes.tictactoe.game.GameMode;
import com.tfdsimoes.tictactoe.game.Scoreboard;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class GamePanel extends JPanel {
    private static final int COMPUTER_MOVE_DELAY_MS = 1_000;
    private static final int ROUND_RESTART_DELAY_MS = 3_000;

    private final GameMode mode;
    private final Scoreboard scoreboard;
    private final MoveStrategy moveStrategy;
    private final Runnable onMainMenu;
    private final List<JButton> cellButtons = new ArrayList<>(9);
    private final JLabel statusLabel = new JLabel("", JLabel.CENTER);
    private final JLabel xScoreLabel = new JLabel("", JLabel.CENTER);
    private final JLabel oScoreLabel = new JLabel("", JLabel.CENTER);
    private final JLabel drawScoreLabel = new JLabel("", JLabel.CENTER);
    private final Timer computerMoveTimer;
    private final Timer roundRestartTimer;

    private GameBoard board;
    private Mark roundStarter = Mark.X;

    GamePanel(GameMode mode, Scoreboard scoreboard, MoveStrategy moveStrategy, Runnable onMainMenu) {
        this.mode = Objects.requireNonNull(mode, "mode");
        this.scoreboard = Objects.requireNonNull(scoreboard, "scoreboard");
        this.moveStrategy = Objects.requireNonNull(moveStrategy, "moveStrategy");
        this.onMainMenu = Objects.requireNonNull(onMainMenu, "onMainMenu");
        this.board = GameBoard.newGame(roundStarter);

        computerMoveTimer = new Timer(COMPUTER_MOVE_DELAY_MS, this::playComputerMove);
        computerMoveTimer.setRepeats(false);
        roundRestartTimer = new Timer(ROUND_RESTART_DELAY_MS, event -> startNextRound());
        roundRestartTimer.setRepeats(false);

        setBackground(UiTheme.BACKGROUND);
        setLayout(new BorderLayout(24, 24));
        setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBoard(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        renderBoard();
        updateTurn();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 14));
        header.setOpaque(false);

        JButton backButton = new JButton("Main menu");
        backButton.setForeground(UiTheme.MUTED);
        backButton.setBackground(UiTheme.SURFACE);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        backButton.addActionListener(event -> {
            computerMoveTimer.stop();
            roundRestartTimer.stop();
            onMainMenu.run();
        });

        JLabel modeLabel = new JLabel(mode.getDisplayName(), JLabel.CENTER);
        modeLabel.setForeground(UiTheme.TEXT);
        modeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 23));
        header.add(backButton, BorderLayout.WEST);
        header.add(modeLabel, BorderLayout.CENTER);

        JPanel scores = new JPanel(new GridLayout(1, 3, 12, 0));
        scores.setOpaque(false);
        styleScoreLabel(xScoreLabel, UiTheme.X_MARK);
        styleScoreLabel(oScoreLabel, UiTheme.O_MARK);
        styleScoreLabel(drawScoreLabel, UiTheme.TEXT);
        scores.add(xScoreLabel);
        scores.add(oScoreLabel);
        scores.add(drawScoreLabel);
        header.add(scores, BorderLayout.SOUTH);
        updateScoreLabels();
        return header;
    }

    private JPanel buildBoard() {
        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 12, 12));
        boardPanel.setOpaque(false);
        boardPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        for (int index = 0; index < 9; index++) {
            final int cellIndex = index;
            JButton cell = new JButton();
            cell.setBackground(UiTheme.SURFACE);
            cell.setForeground(UiTheme.TEXT);
            cell.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 70));
            cell.setBorder(BorderFactory.createLineBorder(UiTheme.SURFACE_LIGHT, 2));
            cell.getAccessibleContext().setAccessibleName(cellName(index));
            cell.getAccessibleContext().setAccessibleDescription("Empty game cell");
            cell.addActionListener(event -> playHumanMove(cellIndex));
            cellButtons.add(cell);
            boardPanel.add(cell);
        }
        return boardPanel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        statusLabel.setForeground(UiTheme.MUTED);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        footer.add(statusLabel, BorderLayout.CENTER);
        return footer;
    }

    private void playHumanMove(int index) {
        if (board.getStatus().isFinished() || !mode.isHumanTurn(board.getNextMark())) {
            return;
        }
        applyMove(index);
    }

    private void playComputerMove(ActionEvent event) {
        if (board.getStatus().isFinished() || mode.isHumanTurn(board.getNextMark())) {
            return;
        }
        int move = moveStrategy.chooseMove(board);
        applyMove(move);
    }

    private void applyMove(int index) {
        Mark playedMark = board.getNextMark();
        board = board.play(index);
        JButton playedCell = cellButtons.get(index);
        playedCell.getAccessibleContext().setAccessibleDescription(playedMark.name() + " selected");
        renderBoard();
        if (board.getStatus().isFinished()) {
            finishRound();
        } else {
            updateTurn();
        }
    }

    private void renderBoard() {
        for (int index = 0; index < cellButtons.size(); index++) {
            JButton button = cellButtons.get(index);
            Mark mark = board.getCell(index);
            button.setText(mark == null ? "" : mark.name());
            button.setForeground(mark == Mark.X ? UiTheme.X_MARK : mark == Mark.O ? UiTheme.O_MARK : UiTheme.TEXT);
            button.setEnabled(mark == null
                    && !board.getStatus().isFinished()
                    && mode.isHumanTurn(board.getNextMark()));
            if (mark == null) {
                button.getAccessibleContext().setAccessibleDescription("Empty game cell");
            }
        }
    }

    private void updateTurn() {
        if (!mode.isHumanTurn(board.getNextMark())) {
            statusLabel.setText(mode.getPlayerName(board.getNextMark())
                    + " playing " + board.getNextMark() + "...");
            renderBoard();
            computerMoveTimer.restart();
            return;
        }

        statusLabel.setText(board.getNextMark() + " to move - " + mode.getPlayerName(board.getNextMark()));
        renderBoard();
    }

    private void finishRound() {
        GameStatus result = board.getStatus();
        scoreboard.record(result);
        updateScoreLabels();
        String message = result.winner()
                .map(mark -> mark + " wins! (" + mode.getPlayerName(mark) + ")")
                .orElse("It's a draw!");
        statusLabel.setText(message + " - Next round in 3 seconds");
        renderBoard();
        roundRestartTimer.restart();
    }

    private void startNextRound() {
        roundStarter = roundStarter.opposite();
        board = GameBoard.newGame(roundStarter);
        renderBoard();
        updateTurn();
    }

    private void updateScoreLabels() {
        xScoreLabel.setText("<html><b>X</b> &nbsp;" + scoreboard.getWins(Mark.X)
                + "<br><span style='color:#9dacc2'>" + mode.getPlayerName(Mark.X) + "</span></html>");
        oScoreLabel.setText("<html><b>O</b> &nbsp;" + scoreboard.getWins(Mark.O)
                + "<br><span style='color:#9dacc2'>" + mode.getPlayerName(Mark.O) + "</span></html>");
        drawScoreLabel.setText("<html><b>Draws</b><br>" + scoreboard.getDraws() + "</html>");
    }

    private static void styleScoreLabel(JLabel label, Color accent) {
        label.setOpaque(true);
        label.setBackground(UiTheme.SURFACE);
        label.setForeground(accent);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        label.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
    }

    private static String cellName(int index) {
        String[] columns = {"left", "center", "right"};
        String[] rows = {"top", "middle", "bottom"};
        return rows[index / 3] + "-" + columns[index % 3] + " cell";
    }
}
