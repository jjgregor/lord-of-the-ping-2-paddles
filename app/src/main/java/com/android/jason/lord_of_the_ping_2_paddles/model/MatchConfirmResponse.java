package com.android.jason.lord_of_the_ping_2_paddles.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by gregjas on 6/22/16.
 */

public class MatchConfirmResponse implements Serializable {

    private boolean confirmed;
    private String pendingId;

    public MatchConfirmResponse(@JsonProperty("pendingId") String pendingId,@JsonProperty("confirmation") boolean confirmed) {
        this.pendingId = pendingId;
        this.confirmed = confirmed;
    }

    public String getPendingId() {
        return pendingId;
    }

    public void setPendingId(String pendingId) {
        this.pendingId = pendingId;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }


}
