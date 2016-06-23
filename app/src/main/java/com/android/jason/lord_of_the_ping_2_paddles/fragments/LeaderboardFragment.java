package com.android.jason.lord_of_the_ping_2_paddles.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.android.jason.lord_of_the_ping_2_paddles.activities.ProfileActivity;
import com.android.jason.lord_of_the_ping_2_paddles.adapters.LeaderboardAdapter;
import com.android.jason.lord_of_the_ping_2_paddles.model.LeaderboardItem;
import com.android.jason.lord_of_the_ping_2_paddles.model.Player;

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

public class LeaderboardFragment extends Fragment {
    public LeaderboardFragment() {

    }

    @BindView(R.id.leaderboard_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.leaderboard_progress)
    ProgressBar progressBar;

    @BindView(R.id.leaderboard_empty)
    TextView tvEmpty;

    private LeaderboardAdapter adapter;
    private LinearLayoutManager llm;

    private ArrayList<LeaderboardItem> mLeaderboard;
    private PingPongApplication app;

    private static final String TAG = LeaderboardFragment.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void refreshData() {

        try {
            Call<List<LeaderboardItem>> items = app.getPingPongService().getLeaderBoard();
            items.enqueue(new Callback<List<LeaderboardItem>>() {
                @Override
                public void onResponse(Call<List<LeaderboardItem>> leaderboard, Response<List<LeaderboardItem>> response) {
                    Log.d(TAG, "Received a leaderboard!");
                    // Why does this want to be casted?
                    if (response.isSuccessful() && response.body() != null) {
                        mLeaderboard = new ArrayList<LeaderboardItem>(response.body());
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        bindLeaderboard();
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<List<LeaderboardItem>> call, Throwable t) {
                    Log.d(TAG, "FAILURE!!!");
                    progressBar.setVisibility(View.INVISIBLE);
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Exception: ", e);
        }

    }


    private void bindLeaderboard() {
        progressBar.setVisibility(View.INVISIBLE);
        llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        adapter = new LeaderboardAdapter(mLeaderboard, new LeaderboardAdapter.LeaderboardItemClickListener() {

            @Override
            public void onItemClick(Player player) {
//                startActivity(new Intent(getActivity(), ProfileActivity.class)
//                        .putExtra(ProfileActivity.PLAYER, player));
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        ButterKnife.bind(this, root);
        progressBar.setVisibility(View.VISIBLE);

        app = (PingPongApplication) getActivity().getApplication();

        llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        refreshData();

        return root;
    }
}
