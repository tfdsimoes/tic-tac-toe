package com.tfdsimoes.tictactoe.game;

import com.tfdsimoes.tictactoe.domain.Mark;

import java.util.Objects;

public enum GameMode {
    PLAYER_VS_PLAYER("Player vs Player", "Two players share the keyboard and take turns"),
    PLAYER_VS_COMPUTER("Player vs Machine", "Play against the computer; marks swap each round"),
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

    public boolean isHumanTurn(PlayerId player) {
        Objects.requireNonNull(player, "player");
        return switch (this) {
            case PLAYER_VS_PLAYER -> true;
            case PLAYER_VS_COMPUTER -> player == PlayerId.FIRST;
            case COMPUTER_VS_COMPUTER -> false;
        };
    }

    public String getPlayerName(PlayerId player) {
        Objects.requireNonNull(player, "player");
        return switch (this) {
            case PLAYER_VS_PLAYER -> player == PlayerId.FIRST ? "Player 1" : "Player 2";
            case PLAYER_VS_COMPUTER -> player == PlayerId.FIRST ? "You" : "Machine";
            case COMPUTER_VS_COMPUTER -> player == PlayerId.FIRST ? "Machine 1" : "Machine 2";
        };
    }
}
