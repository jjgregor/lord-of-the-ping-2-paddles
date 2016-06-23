package com.android.jason.lord_of_the_ping_2_paddles.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.android.jason.lord_of_the_ping_2_paddles.PingPongApplication;
import com.android.jason.lord_of_the_ping_2_paddles.model.Match;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gregjas on 6/22/16.
 */

public class SaveMatchFragment extends Fragment {
    private static final String MATCH = "match";

    public static interface SaveMatchCallbacks {
        void saveMatchSuccessful(String matchId);

        void saveMatchFailed();
    }

    private SaveMatchCallbacks mCallbacks;
    private String matchResponse;

    public static SaveMatchFragment newInstance(Match match) {
        Bundle args = new Bundle();
        args.putSerializable(MATCH, match);
        SaveMatchFragment fragment = new SaveMatchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PingPongApplication app = (PingPongApplication) getActivity().getApplication();
        Match m = (Match) getArguments().getSerializable(MATCH);
        final Call<String> match = app.getPingPongService().saveMatch(m);
        match.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    matchResponse = response.body();
                }

                if (mCallbacks != null) {
                    mCallbacks.saveMatchSuccessful(matchResponse);
                }
                detach();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (mCallbacks != null) {
                    mCallbacks.saveMatchFailed();
                }
                detach();
            }
        });
    }

    private void detach() {
        if (isAdded()) {
            getFragmentManager()
                    .beginTransaction()
                    .remove(SaveMatchFragment.this)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void setSaveMatchCallbacks(SaveMatchCallbacks callbacks) {
        mCallbacks = callbacks;
    }

}
