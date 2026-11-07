package com.tfdsimoes.tictactoe.ui;

import com.tfdsimoes.tictactoe.game.GameMode;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

final class MainMenuPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    MainMenuPanel(Consumer<GameMode> onModeSelected) {
        setBackground(UiTheme.BACKGROUND);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(48, 48, 48, 48));
        content.setLayout(new GridLayout(0, 1, 0, 18));

        JLabel eyebrow = new JLabel("THE CLASSIC GAME");
        eyebrow.setForeground(UiTheme.ACCENT);
        eyebrow.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        eyebrow.setHorizontalAlignment(JLabel.CENTER);

        JLabel title = new JLabel("Tic Tac Toe");
        title.setForeground(UiTheme.TEXT);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 42));
        title.setHorizontalAlignment(JLabel.CENTER);

        JLabel subtitle = new JLabel("Choose how you would like to play");
        subtitle.setForeground(UiTheme.MUTED);
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        subtitle.setHorizontalAlignment(JLabel.CENTER);

        content.add(eyebrow);
        content.add(title);
        content.add(subtitle);
        content.add(modeButton(GameMode.PLAYER_VS_PLAYER, onModeSelected));
        content.add(modeButton(GameMode.PLAYER_VS_COMPUTER, onModeSelected));
        content.add(modeButton(GameMode.COMPUTER_VS_COMPUTER, onModeSelected));

        JPanel centered = new JPanel(new BorderLayout());
        centered.setOpaque(false);
        centered.add(content, BorderLayout.CENTER);
        add(centered, BorderLayout.CENTER);
    }

    private JButton modeButton(GameMode mode, Consumer<GameMode> onModeSelected) {
        JButton button = new JButton("<html><div style='text-align:left;padding:8px 4px'>"
                + "<span style='font-size:16pt;font-weight:bold'>" + mode.getDisplayName()
                + "</span><br><span style='color:#9dacc2;font-size:10pt'>"
                + mode.getDescription() + "</span></div></html>");
        button.setForeground(UiTheme.TEXT);
        button.setBackground(UiTheme.SURFACE);
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        button.setHorizontalAlignment(JButton.LEFT);
        button.setPreferredSize(new Dimension(380, 78));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.SURFACE_LIGHT, 1),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        button.getAccessibleContext().setAccessibleName(mode.getDisplayName());
        button.getAccessibleContext().setAccessibleDescription(mode.getDescription());
        button.addActionListener(event -> onModeSelected.accept(mode));
        return button;
    }
}
