package com.android.jason.lord_of_the_ping_2_paddles.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.jason.lord_of_the_ping_2_paddles.PingPongApplication;
import com.android.jason.lord_of_the_ping_2_paddles.R;
import com.android.jason.lord_of_the_ping_2_paddles.adapters.InboxAdapter;
import com.android.jason.lord_of_the_ping_2_paddles.model.Match;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gregjas on 6/22/16.
 */

public class InboxFragment extends DialogFragment {

    public InboxFragment() {

    }

    @BindView(R.id.inbox_empty)
    TextView empty;

    @BindView(R.id.inbox_progress)
    ProgressBar progressBar;

    @BindView(R.id.inbox_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    public static final String TAG = InboxFragment.class.getName();

    private ArrayList<Match> matches;
    private PingPongApplication app;
    private InboxAdapter adapter;
    long playerID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_fragment_inbox, container, false);
        ButterKnife.bind(this, root);
        progressBar.setVisibility(View.VISIBLE);

        app = (PingPongApplication) getActivity().getApplication();

        if(app.getCurrentPlayer() != null) {
            refreshData();
        } else {
            progressBar.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            empty.setText(R.string.please_sign_in);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        return root;
    }

    public void refreshData() {
        playerID = Long.parseLong(app.getCurrentPlayer().getId());
        Call<List<Match>> matchesCall = app.getPingPongService().getPendingMatches(playerID);
        matchesCall.enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful() && !response.body().isEmpty()) {
                    Log.d(TAG, "Recieved pending matches: " + response.body());
                    matches = new ArrayList<>(response.body());
                    progressBar.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    bindInbox();
                } else {
                    Log.d(TAG, "Matches load failure");
                    progressBar.setVisibility(View.INVISIBLE);
                    empty.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                empty.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void bindInbox() {
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        adapter = new InboxAdapter(matches, app);
        recyclerView.setAdapter(adapter);
    }

}
