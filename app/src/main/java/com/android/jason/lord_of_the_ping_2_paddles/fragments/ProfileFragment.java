package com.android.jason.lord_of_the_ping_2_paddles.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.jason.lord_of_the_ping_2_paddles.PingPongApplication;
import com.android.jason.lord_of_the_ping_2_paddles.R;
import com.android.jason.lord_of_the_ping_2_paddles.activities.ProfileActivity;
import com.android.jason.lord_of_the_ping_2_paddles.adapters.MatchesAdapter;
import com.android.jason.lord_of_the_ping_2_paddles.model.Match;
import com.android.jason.lord_of_the_ping_2_paddles.model.Player;
import com.android.jason.lord_of_the_ping_2_paddles.model.Profile;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gregjas on 6/22/16.
 */

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getName();
    private static final String PLAYER = "player";
    private static final String STATE_PROFILE = "profile";

    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;

    @BindView(R.id.profile_name)
    TextView name;

    @BindView(R.id.profile_matches_lbl)
    TextView matchWinsLbl;

    @BindView(R.id.profile_total_matches)
    TextView totalMatches;

    @BindView(R.id.profile_games_lbl)
    TextView gameWinsLbl;

    @BindView(R.id.profile_total_games)
    TextView totalGames;

    @BindView(R.id.profile_matches_graph)
    PieGraph matchesGraph;

    @BindView(R.id.profile_match_win_perc)
    TextView matchWinPerc;

    @BindView(R.id.profile_match_wins)
    TextView matchWins;

    @BindView(R.id.profile_games_graph)
    PieGraph gamesGraph;

    @BindView(R.id.profile_game_win_perc)
    TextView gameWinPerc;

    @BindView(R.id.profile_game_wins)
    TextView gameWins;

    @BindView(R.id.profile_avatar)
    ImageView avatar;

    @BindView(R.id.match_history_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.profile_progressbar)
    ProgressBar progressBar;

    @BindView(R.id.profile_empty)
    TextView profileEmpty;

    private Profile profile;
    private Player player;
    private PingPongApplication app;
    private List<Match> matches;

    private MatchesAdapter adapter;
    private LinearLayoutManager llm;


    public static ProfileFragment newInstance(Player player) {
        Bundle args = new Bundle();
        args.putSerializable(PLAYER, player);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            profile = (Profile) savedInstanceState.getSerializable(STATE_PROFILE);
        }
        player = (Player) getArguments().getSerializable(PLAYER);
        app = (PingPongApplication) getActivity().getApplication();
        name.setText(player.getName());
        String avatarUrl;
        if (StringUtils.isNotEmpty(avatarUrl = player.getAvatarUrl())) {
            Picasso.with(getActivity()).load(avatarUrl).into(avatar);
        }

        progressBar.setVisibility(View.VISIBLE);

        refreshData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void refreshData() {
        Call<Profile> p = app.getPingPongService().getProfile(Long.parseLong(player.getId()));
        p.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.v(TAG, "Get profile successful: " + response.raw());
                if (response.isSuccessful()) {
                    profile = response.body();
                    matches = profile.getMatches();
                    progressBar.setVisibility(View.INVISIBLE);
                    mainContent.setVisibility(View.VISIBLE);
                    bindProfile();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    profileEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.v(TAG, "Get profile failure: " + t.toString());
            }
        });

    }

    private void bindProfile() {
        bindMatchTotals();
        bindGameTotals();
        bindMatchHistory();
    }

    private void bindMatchTotals() {
        final int wins = profile.getStats().getMatchWins();
        final int losses = profile.getStats().getMatchLosses();
        final int matchesTotal = wins + losses;
        final int winPerc = (int) (((double) wins / (double) matchesTotal * 100));

        totalMatches.setText(String.valueOf(matchesTotal));
        matchWins.setText(getResources().getQuantityString(R.plurals.wins, wins, wins));
        matchesGraph.removeSlices();
        PieSlice winSlice = new PieSlice();
        winSlice.setColor(getResources().getColor(R.color.green));
        winSlice.setValue(wins);
        PieSlice lossSlice = new PieSlice();
        lossSlice.setColor(getResources().getColor(R.color.red));
        lossSlice.setValue(losses);
        matchesGraph.addSlice(winSlice);
        matchesGraph.addSlice(lossSlice);
        matchWinPerc.setText(winPerc + "%");
    }

    private void bindGameTotals() {
        final int wins = profile.getStats().getGameWins();
        final int losses = profile.getStats().getGameLosses();
        final int gameTotal = wins + losses;
        final int winPerc = (int) (((double) wins / (double) gameTotal * 100));

        totalGames.setText(String.valueOf(gameTotal));
        gameWins.setText(getResources().getQuantityString(R.plurals.wins, wins, wins));
        gamesGraph.removeSlices();
        PieSlice winSlice = new PieSlice();
        winSlice.setColor(getResources().getColor(R.color.green));
        winSlice.setValue(wins);
        PieSlice lossSlice = new PieSlice();
        lossSlice.setColor(getResources().getColor(R.color.red));
        lossSlice.setValue(losses);
        gamesGraph.addSlice(winSlice);
        gamesGraph.addSlice(lossSlice);
        gameWinPerc.setText(winPerc + "%");
    }

    private void bindMatchHistory() {
        llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        adapter = new MatchesAdapter(matches, new MatchesAdapter.MatchesItemClickListener() {
            @Override
            public void onItemClick(Player player) {
                startActivity(new Intent(getActivity(), ProfileActivity.class)
                        .putExtra(ProfileActivity.PLAYER, player));
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
