package com.android.jason.lord_of_the_ping_2_paddles.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.jason.lord_of_the_ping_2_paddles.R;
import com.android.jason.lord_of_the_ping_2_paddles.model.LeaderboardItem;
import com.android.jason.lord_of_the_ping_2_paddles.model.Player;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by gregjas on 6/22/16.
 */

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private final List<LeaderboardItem> data;
    private LeaderboardItemClickListener listener;
    private static final String TAG = LeaderboardAdapter.class.getName();

    public LeaderboardAdapter(List<LeaderboardItem> leaderboardItems, LeaderboardItemClickListener listener) {
        this.data = leaderboardItems;
        this.listener = listener;
    }

    public interface LeaderboardItemClickListener {
        void onItemClick(Player player);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements Unbinder {
        private final Unbinder unbinder;

        @BindView(R.id.leaderboard_cardview)
        CardView cardView;

        @BindView(R.id.leaderboard_pos)
        TextView position;

        @BindView(R.id.leaderboard_rating)
        TextView ranking;

        @BindView(R.id.leaderboard_name)
        TextView name;

        @BindView(R.id.leaderboard_wins)
        TextView wins;

        @BindView(R.id.leaderboard_losses)
        TextView losses;

        public ViewHolder(final View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

        @Override
        public void unbind() {
            if (unbinder != null) {
                unbinder.unbind();
            }
        }
    }

    @Override
    public LeaderboardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_cardview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LeaderboardAdapter.ViewHolder holder, int position) {

        if (data == null) {
            return;
        }

        final LeaderboardItem item = data.get(position);

        if (!TextUtils.isEmpty((item.getPlayer().getName()))) {
            holder.name.setText(item.getPlayer().getName());
        }
        if (!TextUtils.isEmpty(String.valueOf(item.getMatchWins()))) {
            holder.wins.setText(String.valueOf(item.getMatchWins()));
        }
        if (!TextUtils.isEmpty(String.valueOf(item.getMatchLosses()))) {
            holder.losses.setText(String.valueOf(item.getMatchLosses()));
        }
        if (!TextUtils.isEmpty(item.getPlayer().getRanking())) {
            holder.ranking.setText(String.valueOf(item.getPlayer().getRanking()));
        }

        holder.position.setText(String.valueOf(position + 1));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Snackbar.make(v, "Leader Board Player Clicked!!!", Snackbar.LENGTH_LONG).show();
                listener.onItemClick(item.getPlayer());
            }
        });

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
}
