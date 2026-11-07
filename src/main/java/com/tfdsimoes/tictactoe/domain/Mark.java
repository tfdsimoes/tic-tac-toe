package com.tfdsimoes.tictactoe.domain;

public enum Mark {
    X,
    O;

    public Mark opposite() {
        return this == X ? O : X;
    }
}
