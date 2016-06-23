package com.android.jason.lord_of_the_ping_2_paddles.model;

import java.io.Serializable;

/**
 * Created by gregjas on 6/22/16.
 */

public class Stats implements Serializable {
    private int matchWins;
    private int matchLosses;
    private int gameWins;
    private int gameLosses;

    public int getMatchWins() {
        return matchWins;
    }

    public int getMatchLosses() {
        return matchLosses;
    }

    public int getGameWins() {
        return gameWins;
    }

    public int getGameLosses() {
        return gameLosses;
    }
}
