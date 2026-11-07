package com.tfdsimoes.tictactoe.game;

import com.tfdsimoes.tictactoe.domain.Mark;

import java.util.Objects;

public final class PlayerAssignments {
    private final Mark firstPlayerMark;

    private PlayerAssignments(Mark firstPlayerMark) {
        this.firstPlayerMark = Objects.requireNonNull(firstPlayerMark, "firstPlayerMark");
    }

    public static PlayerAssignments initial() {
        return new PlayerAssignments(Mark.X);
    }

    public PlayerAssignments nextRound() {
        return new PlayerAssignments(firstPlayerMark.opposite());
    }

    public Mark markFor(PlayerId player) {
        return Objects.requireNonNull(player, "player") == PlayerId.FIRST
                ? firstPlayerMark
                : firstPlayerMark.opposite();
    }

    public PlayerId playerFor(Mark mark) {
        return Objects.requireNonNull(mark, "mark") == firstPlayerMark
                ? PlayerId.FIRST
                : PlayerId.SECOND;
    }
}
