package com.android.jason.lord_of_the_ping_2_paddles.model;

import java.io.Serializable;

/**
 * Created by gregjas on 6/22/16.
 */

public class MatchConfirmResponse implements Serializable {
    private String pendingId;
    private boolean confirmed;

    public MatchConfirmResponse(String pendingId, boolean confirmed) {
        this.pendingId = pendingId;
        this.confirmed = confirmed;
    }
}
