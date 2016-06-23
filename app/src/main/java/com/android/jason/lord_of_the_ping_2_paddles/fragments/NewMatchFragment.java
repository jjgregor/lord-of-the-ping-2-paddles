package com.android.jason.lord_of_the_ping_2_paddles.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.jason.lord_of_the_ping_2_paddles.PingPongApplication;
import com.android.jason.lord_of_the_ping_2_paddles.R;
import com.android.jason.lord_of_the_ping_2_paddles.model.Match;
import com.android.jason.lord_of_the_ping_2_paddles.model.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gregjas on 6/22/16.
 */

public class NewMatchFragment extends Fragment  implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, SaveMatchFragment.SaveMatchCallbacks, AuthFragment.AuthCallbacks  {
    private static final String TAG = NewMatchFragment.class.getName();
    private static final String STATE_OPPONENT = "opponent";
    private static final String STATE_OPPONENTS = "opponents";
    private static final DateFormat MATCH_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    private static final String[] WINS_ARRAY = {"0", "1", "2", "3", "4", "5"};

    private static final String[][] LOSSES_ARRAYS = {

            // 0 WINS (loss)
            {"3", "4", "5"},

            // 1 WIN (loss)
            {"3", "4"},

            // 2 WINS (loss)
            {"3"},

            // 3 WINS (win)
            {"0", "1", "2"},

            // 4 WINS (win)
            {"0", "1"},

            // 5 WINS (win)
            {"0"},
    };


    @BindView(R.id.new_match_cont)
    ViewGroup mNewMatchCont;

    @BindView(R.id.new_match_opponent)
    AutoCompleteTextView mOpponentField;

    @BindView(R.id.new_match_clear_opponent)
    ImageButton mClearOpponentBtn;

    @BindView(R.id.new_match_confirmed_opponent)
    ViewGroup mConfirmedOpponentCont;

    @BindView(R.id.opponent_name)
    TextView mConfirmedOpponentName;

    @BindView(R.id.opponent_email)
    TextView mConfirmedOpponentEmail;

    @BindView(R.id.new_match_wins)
    Spinner mWinsSpinner;

    @BindView(R.id.new_match_losses)
    Spinner mLossesSpinner;

    @BindView(R.id.new_match_result)
    TextView mResult;

    @BindView(R.id.new_match_submit_btn)
    Button mSubmitBtn;

    @BindView(R.id.new_match_submitting_cont)
    ViewGroup mSubmittingCont;

    @BindView(R.id.new_match_match_submission_result_cont)
    ViewGroup mSubmissionResultCont;

    @BindView(R.id.new_match_submission_result)
    TextView mSubmissionResult;

    @BindView(R.id.new_match_submission_continue_btn)
    Button mSubmissionContinueBtn;

    private OpponentAutoCompleteAdapter mOpponentAutoCompleteAdapter;
    private List<Player> mOpponents;

    private PingPongApplication app;
    private Player mOpponent;
    private Match mPendingMatch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_match, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mOpponent = (Player) savedInstanceState.getSerializable(STATE_OPPONENT);
            mOpponents = (ArrayList<Player>) savedInstanceState.getSerializable(STATE_OPPONENTS);
        }
        app = (PingPongApplication) getActivity().getApplication();

        // maybe re-attach to SaveMatchFragment
        SaveMatchFragment saveMatchFragment;
        if ((saveMatchFragment = (SaveMatchFragment) getFragmentManager().findFragmentByTag(SaveMatchFragment.class.getName())) != null) {
            saveMatchFragment.setSaveMatchCallbacks(this);
        }

        // maybe re-attach to AuthFragment
        AuthFragment authFragment;
        if ((authFragment = (AuthFragment) getFragmentManager().findFragmentByTag(AuthFragment.class.getName())) != null) {
            authFragment.setAuthCallbacks(this);
        }

        if (mOpponents == null) {
            getOpponents();
        }

        mOpponentAutoCompleteAdapter = new OpponentAutoCompleteAdapter(getActivity(), mOpponents);
        mOpponentField.setAdapter(mOpponentAutoCompleteAdapter);
        mOpponentField.setOnItemClickListener(this);

        mResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // toggle between wins and losses
                if (getTotalWins() >= 3) {
                    mWinsSpinner.setSelection(0);
                } else {
                    mWinsSpinner.setSelection(3);
                }
            }
        });

        mClearOpponentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearOpponent();
            }
        });

        mWinsSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.spinner_win_losses_dropdown_item, WINS_ARRAY));
        mWinsSpinner.setOnItemSelectedListener(this);

        mLossesSpinner.setOnItemSelectedListener(this);

        if (mOpponent != null) {
            bindOpponent();
        } else {

            // default to win
            mWinsSpinner.setSelection(3);
        }

        mOpponentField.setVisibility(mOpponent != null ? View.GONE : View.VISIBLE);
        mConfirmedOpponentCont.setVisibility(mOpponent != null ? View.VISIBLE : View.GONE);

        updateOpponentState();
        updateWinsSpinnerState();
    }

    @OnClick(R.id.new_match_submit_btn)
    public void onSumbitClicked() {
        if (app.getCurrentPlayer() == null) {

            // build a pending match
            mPendingMatch = buildMatchFromUi();

            AuthFragment fragment = new AuthFragment();
            fragment.setAuthCallbacks(NewMatchFragment.this);
            getFragmentManager()
                    .beginTransaction()
                    .add(fragment, AuthFragment.class.getName())
                    .commit();

        } else {
            submitMatch(buildMatchFromUi());
        }
    }

    private void getOpponents() {
        mOpponents = new ArrayList<Player>();
        Call<List<Player>> opponents = app.getPingPongService().getAllPlayers();
        opponents.enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful()) {
                    mOpponents = response.body();
                    mOpponentAutoCompleteAdapter.setOpponents(mOpponents);
                    mOpponentAutoCompleteAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {

            }
        });
    }

    private void submitMatch(final Match match) {
        mNewMatchCont
                .animate()
                .alpha(0)
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        mSubmittingCont.setAlpha(0);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mNewMatchCont.setVisibility(View.GONE);
                        mSubmittingCont.setVisibility(View.VISIBLE);
                        mSubmittingCont
                                .animate()
                                .alpha(1)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        saveMatch(match);
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    private Match buildMatchFromUi() {
        Match match = new Match();
        match.setPlayerOne(app.getCurrentPlayer());
        match.setPlayerTwo(mOpponent);
        match.setP1Score(getTotalWins());
        match.setP2Score(getTotalLosses());
        match.setDateString(MATCH_DATE_FORMAT.format(new Date()));
        return match;
    }

    private int getTotalWins() {
        return Integer.valueOf(((ArrayAdapter<String>) mWinsSpinner.getAdapter()).getItem(mWinsSpinner.getSelectedItemPosition()));
    }

    private int getTotalLosses() {
        return Integer.valueOf(((ArrayAdapter<String>) mLossesSpinner.getAdapter()).getItem(mLossesSpinner.getSelectedItemPosition()));
    }

    private void saveMatch(Match match) {
        SaveMatchFragment fragment = SaveMatchFragment.newInstance(match);
        fragment.setSaveMatchCallbacks(NewMatchFragment.this);

        getFragmentManager()
                .beginTransaction()
                .add(fragment, SaveMatchFragment.class.getName())
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_OPPONENT, mOpponent);
    }

    private void updateWinsSpinnerState() {
        mWinsSpinner.setEnabled(isValidOpponent());
        updateLossesSpinnerState();
        updateSubmitBtnState();
    }

    private void updateConfirmedOpponent() {
        mOpponentField
                .animate()
                .alpha(0)
                .setListener(
                        new AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationStart(Animator animation) {
                                mConfirmedOpponentCont.setAlpha(0);
                                bindOpponent();
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mOpponentField.setVisibility(View.GONE);
                                mConfirmedOpponentCont.setVisibility(View.VISIBLE);
                                mConfirmedOpponentCont
                                        .animate()
                                        .alpha(1)
                                        .setListener(
                                                new AnimatorListenerAdapter() {

                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        mClearOpponentBtn.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                        )
                                        .start();
                            }
                        }
                )
                .start();
    }

    private void bindOpponent() {
        if (mOpponent != null) {
            mConfirmedOpponentName.setText(mOpponent.getName());
            mConfirmedOpponentEmail.setText(mOpponent.getEmail());
            mClearOpponentBtn.setVisibility(View.VISIBLE);
        } else {
            mOpponentField.setVisibility(View.VISIBLE);
            mConfirmedOpponentCont.setVisibility(View.GONE);
            mClearOpponentBtn.setVisibility(View.GONE);
        }
    }

    private void updateOpponentState() {
        mOpponentField.setEnabled(!isValidOpponent());
    }

    private void updateLossesSpinnerState() {
        mLossesSpinner.setEnabled(isValidOpponent());
    }

    private void updateSubmitBtnState() {
        mSubmitBtn.setEnabled(isValidOpponent());
    }

    private boolean isValidOpponent() {
        return mOpponent != null && !mOpponent.equals(app.getCurrentPlayer());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mOpponent = mOpponentAutoCompleteAdapter.getItem(position);
        Log.d(TAG, "Selected player " + mOpponent + " as opponent");
        updateConfirmedOpponent();
        updateOpponentState();
        updateWinsSpinnerState();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mWinsSpinner) {
            mLossesSpinner.setAdapter(
                    new ArrayAdapter<String>(
                            getActivity(),
                            R.layout.spinner_win_losses_dropdown_item,
                            LOSSES_ARRAYS[Integer.valueOf(WINS_ARRAY[position])]
                    )
            );
        }
        updateResult();
    }

    private void updateResult() {
        final boolean win = getTotalWins() > getTotalLosses();
        mResult.setText(win ? "W" : "L");
        mResult.setBackgroundResource(win ? R.drawable.win_background : R.drawable.loss_background);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {/* no-op */}

    @Override
    public void saveMatchSuccessful(String matchId) {
        showResultCont(getString(R.string.new_match_match_submitted_lbl, mOpponent.getName()), true);
    }

    @Override
    public void saveMatchFailed() {
        showResultCont(getString(R.string.new_match_match_submission_failed_lbl), false);
    }

    private void showResultCont(final String result, final boolean success) {
        mSubmittingCont
                .animate()
                .alpha(0)
                .setListener(
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                mSubmissionResultCont.setAlpha(0);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mSubmittingCont.setVisibility(View.GONE);
                                mSubmissionResultCont.setVisibility(View.VISIBLE);
                                mSubmissionResult.setText(result);
                                mSubmittingCont.animate().setListener(null);
                                mSubmissionResultCont
                                        .animate()
                                        .alpha(1)
                                        .setListener(
                                                new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        mSubmissionContinueBtn.setOnClickListener(
                                                                new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        mSubmissionResultCont
                                                                                .animate()
                                                                                .alpha(0)
                                                                                .setListener(
                                                                                        new AnimatorListenerAdapter() {

                                                                                            @Override
                                                                                            public void onAnimationEnd(Animator animation) {
                                                                                                mSubmissionResultCont.setVisibility(View.GONE);
                                                                                                mNewMatchCont.setVisibility(View.VISIBLE);
                                                                                                mNewMatchCont
                                                                                                        .animate()
                                                                                                        .alpha(1)
                                                                                                        .setListener(
                                                                                                                new AnimatorListenerAdapter() {
                                                                                                                    @Override
                                                                                                                    public void onAnimationEnd(Animator animation) {
                                                                                                                        if (success) {
                                                                                                                            continueWithSuccess();
                                                                                                                        } else {
                                                                                                                            continueWithFailure();
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                        );
                                                                                            }
                                                                                        }
                                                                                );
                                                                    }
                                                                }
                                                        );
                                                    }
                                                }
                                        )
                                        .start();
                            }
                        }
                );
    }

    private void continueWithSuccess() {
        clearOpponent();
    }

    private void clearOpponent() {
        mOpponent = null;
        mOpponentField.setText(null);
        mConfirmedOpponentCont
                .animate()
                .alpha(0)
                .setListener(
                        new AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationStart(Animator animation) {
                                mClearOpponentBtn.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mConfirmedOpponentName.setText(null);
                                mConfirmedOpponentEmail.setText(null);
                                mConfirmedOpponentCont.setVisibility(View.GONE);
                                mOpponentField.setVisibility(View.VISIBLE);
                                mOpponentField
                                        .animate()
                                        .alpha(1)
                                        .setListener(null)
                                        .start();
                            }
                        }
                )
                .start();
        updateOpponentState();
        updateWinsSpinnerState();
    }

    private void continueWithFailure() {
        // do nothing?
    }

    @Override
    public void playerSignedIn(Player player) {
        if (mPendingMatch != null) {
            if (player.equals(mPendingMatch.getPlayerTwo())) {
                mNewMatchCont
                        .animate()
                        .alpha(0)
                        .setListener(
                                new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        showResultCont(getString(R.string.new_match_submission_failed_same_player), false);

                                        mOpponent = null;
                                        mOpponentField.setText(null);
                                        updateOpponentState();
                                        bindOpponent();
                                    }
                                }
                        )
                        .start();
            } else {
                mPendingMatch.setPlayerOne(player);
                submitMatch(mPendingMatch);
            }
        }
    }

    @Override
    public void authCancelled() {
        // TODO
    }

    private static class OpponentAutoCompleteAdapter extends ArrayAdapter<Player> implements Filterable {
        private static final int LAYOUT_RES_ID = R.layout.item_opponent;
        private LayoutInflater mInflater;
        private List<Player> mOpponents;

        public OpponentAutoCompleteAdapter(Context context, List<Player> opponents) {
            super(context, LAYOUT_RES_ID, opponents);
            mOpponents = opponents;
            mInflater = LayoutInflater.from(context);
        }

        public void setOpponents(List<Player> opponents) {
            mOpponents = opponents;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(LAYOUT_RES_ID, parent, false);
            }
            ImageView avatar = (ImageView) convertView.findViewById(R.id.opponent_avatar);
            TextView name = (TextView) convertView.findViewById(R.id.opponent_name);
            TextView email = (TextView) convertView.findViewById(R.id.opponent_email);

            final Player opponent = getItem(position);
            name.setText(opponent.getName());
            email.setText(opponent.getEmail());
            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter myFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(final CharSequence constraint) {
                    final FilterResults filterResults = new FilterResults();
                    List<Player> filteredPlayers = new ArrayList<Player>();
                    final Player currentPlayer = ((PingPongApplication) getContext().getApplicationContext()).getCurrentPlayer();
                    for (Player player : mOpponents) {
                        if (currentPlayer != null && currentPlayer.equals(player)) {
                            continue;
                        }
                        if (constraint != null || player.getName().toUpperCase().startsWith(constraint.toString().toUpperCase())
                                || player.getEmail().toUpperCase().contains(constraint.toString().toUpperCase())) {
                            filteredPlayers.add(player);
                        }
                    }
                    filterResults.values = filteredPlayers;
                    filterResults.count = filteredPlayers.size();

                    return filterResults;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(final CharSequence contraint, final FilterResults results) {
                    clear();
                    if (results.values == null) {
                        return;
                    }
                    for (Player player : (List<Player>) results.values) {
                        if (player != null) {
                            add(player);
                        }
                    }
                    if (results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }

                @Override
                public CharSequence convertResultToString(final Object resultValue) {
                    return resultValue == null ? "" : ((Player) resultValue).getName();
                }
            };
            return myFilter;
        }
    }
}
