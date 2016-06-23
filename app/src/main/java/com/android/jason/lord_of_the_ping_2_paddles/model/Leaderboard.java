package com.android.jason.lord_of_the_ping_2_paddles.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gregjas on 6/22/16.
 */

public class Leaderboard implements Serializable {
    private List<LeaderboardItem> leaderBoardItems;

    public List<LeaderboardItem> getLeaderBoardItems() {
        return leaderBoardItems;
    }

    public void setLeaderBoardItems(List<LeaderboardItem> leaderBoardItems) {
        this.leaderBoardItems = leaderBoardItems;
    }

}
