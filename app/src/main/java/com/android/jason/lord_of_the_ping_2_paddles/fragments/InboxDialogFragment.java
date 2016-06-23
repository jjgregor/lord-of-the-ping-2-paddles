package com.android.jason.lord_of_the_ping_2_paddles.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;

import com.android.jason.lord_of_the_ping_2_paddles.PingPongApplication;
import com.android.jason.lord_of_the_ping_2_paddles.model.Match;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gregjas on 6/22/16.
 */

public class InboxDialogFragment extends DialogFragment {

    public InboxDialogFragment() {

    }

    private LinearLayoutManager llm;
    private ArrayList<Match> matches;
    private PingPongApplication app;
    long playerID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void refreshData(){
        playerID = Long.parseLong(app.getCurrentPlayer().getId());
        Call<List<Match>> matchesCall = app.getPingPongService().getPendingMatches(playerID);
        matchesCall.enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if(response.isSuccessful() && response.body() != null){
                    matches = new ArrayList<Match>(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {

            }
        });
    }

}
