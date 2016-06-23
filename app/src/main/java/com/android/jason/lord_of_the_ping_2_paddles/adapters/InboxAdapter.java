package com.android.jason.lord_of_the_ping_2_paddles.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.jason.lord_of_the_ping_2_paddles.R;
import com.android.jason.lord_of_the_ping_2_paddles.model.Match;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by gregjas on 6/23/16.
 */

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private final List<Match> data;
    private static final String TAG = InboxAdapter.class.getName();

    public InboxAdapter(List<Match> matches) {
        this.data = matches;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_cardview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(InboxAdapter.ViewHolder holder, int position) {

        if(data == null){
            return;
        }

        final Match match = data.get(position);

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
