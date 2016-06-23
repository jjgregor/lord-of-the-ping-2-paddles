package com.android.jason.lord_of_the_ping_2_paddles.model;

import java.io.Serializable;

/**
 * Created by gregjas on 6/22/16.
 */

public class LeaderboardItem implements Serializable {
    private Player player;
    private int matchWins;
    private int matchLosses;
    private double winningPercentage;
    private int ranking;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getMatchWins() {
        return matchWins;
    }

    public void setMatchWins(int matchWins) {
        this.matchWins = matchWins;
    }

    public int getMatchLosses() {
        return matchLosses;
    }

    public void setMatchLosses(int matchLosses) {
        this.matchLosses = matchLosses;
    }

    public double getWinningPercentage() {
        return winningPercentage;
    }

    public void setWinningPercentage(double winningPercentage) {
        this.winningPercentage = winningPercentage;
    }

    public int getRanking() { return ranking; }

    public void setRanking(int ranking) { this.ranking = ranking; }
}
