package com.tfdsimoes.tictactoe.game;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class Scoreboard {
    private final Map<GameMode, Map<PlayerId, Integer>> wins = new EnumMap<>(GameMode.class);
    private final Map<GameMode, Integer> draws = new EnumMap<>(GameMode.class);

    public Scoreboard() {
        for (GameMode mode : GameMode.values()) {
            Map<PlayerId, Integer> modeWins = new EnumMap<>(PlayerId.class);
            for (PlayerId player : PlayerId.values()) {
                modeWins.put(player, 0);
            }
            wins.put(mode, modeWins);
            draws.put(mode, 0);
        }
    }

    public void recordWin(GameMode mode, PlayerId player) {
        Map<PlayerId, Integer> modeWins = winsForMode(mode);
        Objects.requireNonNull(player, "player");
        modeWins.put(player, modeWins.get(player) + 1);
    }

    public void recordDraw(GameMode mode) {
        GameMode checkedMode = Objects.requireNonNull(mode, "mode");
        draws.put(checkedMode, draws.get(checkedMode) + 1);
    }

    public int getWins(GameMode mode, PlayerId player) {
        return winsForMode(mode).get(Objects.requireNonNull(player, "player"));
    }

    public int getDraws(GameMode mode) {
        return draws.get(Objects.requireNonNull(mode, "mode"));
    }

    private Map<PlayerId, Integer> winsForMode(GameMode mode) {
        return wins.get(Objects.requireNonNull(mode, "mode"));
    }
}
