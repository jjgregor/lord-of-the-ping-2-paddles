package com.android.jason.lord_of_the_ping_2_paddles.viewModel;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.support.v7.widget.LinearLayoutManager;

import com.android.jason.lord_of_the_ping_2_paddles.R;
import com.android.jason.lord_of_the_ping_2_paddles.activities.ProfileActivity;
import com.android.jason.lord_of_the_ping_2_paddles.adapters.MatchesAdapter;
import com.android.jason.lord_of_the_ping_2_paddles.databinding.ActivityProfileBinding;
import com.android.jason.lord_of_the_ping_2_paddles.model.Player;
import com.android.jason.lord_of_the_ping_2_paddles.model.Profile;
import com.echo.holographlibrary.PieSlice;

/**
 * Created by gregjas on 6/22/16.
 */

public class ProfileViewModel extends BaseObservable {
    private final ProfileActivity activity;
    private ActivityProfileBinding binding;

    public Profile profile;

    public ProfileViewModel(ProfileActivity activity, ActivityProfileBinding binding) {
        this.activity = activity;
        this.binding = binding;
    }

    public void bindProfile(Profile profile) {
        this.profile = profile;
        bindMatchTotals();
        bindGameTotals();
        bindMatchHistory();
        notifyChange();

    }

    private void bindMatchTotals() {
        final int wins = profile.getStats().getMatchWins();
        final int losses = profile.getStats().getMatchLosses();
        final int matchesTotal = wins + losses;
        final int winPerc = (int) (((double) wins / (double) matchesTotal * 100));

        PieSlice winSlice = new PieSlice();
        winSlice.setColor(activity.getResources().getColor(R.color.green));
        winSlice.setValue(wins);
        PieSlice lossSlice = new PieSlice();
        lossSlice.setColor(activity.getResources().getColor(R.color.red));
        lossSlice.setValue(losses);
        binding.profileTotalMatches.setText(String.valueOf(matchesTotal));
        binding.profileMatchWins.setText(activity.getResources().getQuantityString(R.plurals.wins, wins, wins));
        binding.profileMatchesGraph.removeSlices();
        binding.profileMatchesGraph.addSlice(winSlice);
        binding.profileMatchesGraph.addSlice(lossSlice);
        binding.profileMatchWinPerc.setText(winPerc + "%");
    }

    private void bindGameTotals() {
        final int wins = profile.getStats().getGameWins();
        final int losses = profile.getStats().getGameLosses();
        final int gameTotal = wins + losses;
        final int winPerc = (int) (((double) wins / (double) gameTotal * 100));

        PieSlice winSlice = new PieSlice();
        winSlice.setColor(activity.getResources().getColor(R.color.green));
        winSlice.setValue(wins);
        PieSlice lossSlice = new PieSlice();
        lossSlice.setColor(activity.getResources().getColor(R.color.red));
        lossSlice.setValue(losses);
        binding.profileTotalGames.setText(String.valueOf(gameTotal));
        binding.profileGameWins.setText(activity.getResources().getQuantityString(R.plurals.wins, wins, wins));
        binding.profileGamesGraph.removeSlices();
        binding.profileGamesGraph.addSlice(winSlice);
        binding.profileGamesGraph.addSlice(lossSlice);
        binding.profileGameWinPerc.setText(winPerc + "%");
    }

    private void bindMatchHistory() {
        binding.matchHistoryRecycler.setAdapter(new MatchesAdapter(profile.getMatches()
                , new MatchesAdapter.MatchesItemClickListener() {
            @Override
            public void onItemClick(Player player) {
                activity.startActivity(new Intent(activity, ProfileActivity.class)
                        .putExtra(ProfileActivity.PLAYER, player));
            }
        }));
        binding.matchHistoryRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
    }
}
