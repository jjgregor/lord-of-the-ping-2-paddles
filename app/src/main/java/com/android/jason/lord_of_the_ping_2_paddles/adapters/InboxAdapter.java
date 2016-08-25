package com.android.jason.lord_of_the_ping_2_paddles.adapters;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.jason.lord_of_the_ping_2_paddles.PingPongApplication;
import com.android.jason.lord_of_the_ping_2_paddles.R;
import com.android.jason.lord_of_the_ping_2_paddles.model.Match;
import com.android.jason.lord_of_the_ping_2_paddles.model.MatchConfirmResponse;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gregjas on 6/23/16.
 */

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private final ArrayList<Match> data;
    private final PingPongApplication app;
    private static final String TAG = InboxAdapter.class.getName();

    public InboxAdapter(ArrayList<Match> matches, PingPongApplication application) {
        this.data = matches;
        this.app = application;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements Unbinder {
        private final Unbinder unbinder;

        @BindView(R.id.inbox_cardview)
        CardView cardView;

        @BindView(R.id.inbox_match_confirmation_confirm_btn)
        Button btnConfirm;

        @BindView(R.id.inbox_match_confirmation_decline_btn)
        Button btnDecline;

        @BindView(R.id.inbox_match_confirmation_name)
        TextView confirmName;

        @BindView(R.id.inbox_match_confirmation_result_lbl)
        TextView result;

        @BindView(R.id.inbox_match_confirmation_lbl)
        TextView confirmLbl;

        public ViewHolder(View itemView) {
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
    public InboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_cardview, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(InboxAdapter.ViewHolder holder, int position) {

        if (data == null) {
            return;
        }

        final Match match = data.get(position);

        int wins = match.getP2Score();
        int losses = match.getP1Score();
        boolean win = wins > losses;
        holder.result.setText(win ? "W" : "L");
        holder.result.setBackgroundResource(win ? R.drawable.win_background : R.drawable.loss_background);

        holder.confirmLbl.setText(match.getPlayerOne().getName() + " (" + match.getP1Score() + ", " + match.getP2Score() + ") " + new Date(match.getDate()));

        holder.btnConfirm.setTag(position);
        holder.btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                MatchConfirmResponse confirmResponse = new MatchConfirmResponse(String.valueOf(match.getId()), true);
                Call<MatchConfirmResponse> confirm = app.getPingPongService().confirmMatch(confirmResponse);
                confirm.enqueue(new Callback<MatchConfirmResponse>() {
                    @Override
                    public void onResponse(Call<MatchConfirmResponse> call, Response<MatchConfirmResponse> response) {
                        //refresh inbox
                        Snackbar.make(view, "Match confirmed!", Snackbar.LENGTH_LONG).show();
                        data.remove((int) view.getTag());
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<MatchConfirmResponse> call, Throwable t) {
                        Snackbar.make(view, "Sorry that didn't work :(", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });

        holder.btnDecline.setTag(position);
        holder.btnDecline.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                MatchConfirmResponse declineResponse = new MatchConfirmResponse(String.valueOf(match.getId()), false);
                Call<MatchConfirmResponse> confirm = app.getPingPongService().confirmMatch(declineResponse);
                confirm.enqueue(new Callback<MatchConfirmResponse>() {
                    @Override
                    public void onResponse(Call<MatchConfirmResponse> call, Response<MatchConfirmResponse> response) {
                        //refresh inbox
                        Snackbar.make(view, "Match declined!", Snackbar.LENGTH_LONG).show();
                        data.remove((int) view.getTag());
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<MatchConfirmResponse> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                        Snackbar.make(view, "Sorry that didn't work :(", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
}
