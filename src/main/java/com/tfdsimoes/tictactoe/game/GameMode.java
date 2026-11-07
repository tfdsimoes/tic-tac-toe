package com.tfdsimoes.tictactoe.game;

import com.tfdsimoes.tictactoe.domain.Mark;

public enum GameMode {
    PLAYER_VS_PLAYER("Player vs Player", "Two players share the keyboard and take turns"),
    PLAYER_VS_COMPUTER("Player vs Machine", "Play as X against the computer as O"),
    COMPUTER_VS_COMPUTER("Machine vs Machine", "Watch two computer-controlled players compete");

    private final String displayName;
    private final String description;

    GameMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isHumanTurn(Mark mark) {
        return switch (this) {
            case PLAYER_VS_PLAYER -> true;
            case PLAYER_VS_COMPUTER -> mark == Mark.X;
            case COMPUTER_VS_COMPUTER -> false;
        };
    }

    public String getPlayerName(Mark mark) {
        return switch (this) {
            case PLAYER_VS_PLAYER -> mark == Mark.X ? "Player 1" : "Player 2";
            case PLAYER_VS_COMPUTER -> mark == Mark.X ? "You" : "Machine";
            case COMPUTER_VS_COMPUTER -> mark == Mark.X ? "Machine 1" : "Machine 2";
        };
    }
}
