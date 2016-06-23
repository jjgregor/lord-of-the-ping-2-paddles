package com.android.jason.lord_of_the_ping_2_paddles.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gregjas on 6/22/16.
 */

public class Profile implements Serializable {
    private Player player;
    private Stats stats;
    private List<Match> matches;

    public Player getPlayer() {
        return player;
    }

    public Stats getStats() {
        return stats;
    }

    public List<Match> getMatches() {
        return matches;
    }
}
