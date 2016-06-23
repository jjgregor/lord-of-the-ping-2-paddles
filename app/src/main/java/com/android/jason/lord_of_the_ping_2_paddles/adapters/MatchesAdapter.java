package com.android.jason.lord_of_the_ping_2_paddles.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.jason.lord_of_the_ping_2_paddles.R;
import com.android.jason.lord_of_the_ping_2_paddles.model.Match;
import com.android.jason.lord_of_the_ping_2_paddles.model.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by gregjas on 6/22/16.
 */

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder> {
    private MatchesItemClickListener listener;
    private final List<Match> data;
    private Activity activity;
    public static final String TAG = MatchesAdapter.class.getName();

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd");

    public MatchesAdapter(List<Match> matches, MatchesItemClickListener listener) {
        this.listener = listener;
        this.data = matches;
    }

    public interface MatchesItemClickListener {
        void onItemClick(Player player);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements Unbinder {
        private final Unbinder unbinder;

        @BindView(R.id.match_history_cardview)
        CardView cardView;

        @BindView(R.id.match_history_date)
        TextView date;

        @BindView(R.id.match_history_opponent)
        TextView opponent;

        @BindView(R.id.match_history_result)
        TextView result;

        @BindView(R.id.match_history_score)
        TextView score;

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_history_cardview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Match match = data.get(position);

        Log.d(TAG, "Match Date: " + match.getDate());
        Log.d(TAG, " player 2: " + match.getPlayerTwo().getName());

        if (!TextUtils.isEmpty(String.valueOf(match.getDate()))) {
            holder.date.setText(match.getDateString());
            holder.date.setText(DATE_FORMAT.format(new Date(match.getDate())));
        }
        if (!TextUtils.isEmpty(match.getPlayerTwo().getName())) {
            holder.opponent.setText(match.getPlayerTwo().getName());
        }
        if (!TextUtils.isEmpty(String.valueOf(match.getP1Score()))
                && !TextUtils.isEmpty(String.valueOf(match.getP2Score()))) {
            final boolean win = match.getP1Score() > match.getP2Score();
            holder.result.setText(win ? "W" : "L");
            holder.result.setBackgroundResource(win ? R.drawable.win_background : R.drawable.loss_background);
            holder.score.setText(match.getP1Score() + "-" + match.getP2Score());
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(match.getPlayerTwo());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
}
