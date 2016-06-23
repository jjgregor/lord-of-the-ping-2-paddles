package com.android.jason.lord_of_the_ping_2_paddles.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.jason.lord_of_the_ping_2_paddles.PingPongApplication;
import com.android.jason.lord_of_the_ping_2_paddles.R;
import com.android.jason.lord_of_the_ping_2_paddles.model.Player;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.jason.lord_of_the_ping_2_paddles.fragments.AuthFragment.sha256;

/**
 * Created by gregjas on 6/22/16.
 */

public class SignInFragment extends Fragment {
    private static final String TAG = SignInFragment.class.getName();
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    public static interface SignInCallbacks {
        void signInSuccessful(Player player);

        void signInFailed(String error);
    }

    private SignInCallbacks callbacks;
    private Player player;

    public static SignInFragment newInstance(String email, String password) {
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        args.putString(PASSWORD, password);
        SignInFragment fragment = new SignInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        PingPongApplication app = (PingPongApplication) getActivity().getApplication();
        String email = getArguments().getString(EMAIL);
        String password = getArguments().getString(PASSWORD);
        password = sha256(password);

        final Call<Player> p = app.getPingPongService().signIn(new Player(email, password));
        p.enqueue(new Callback<Player>() {
            @Override
            public void onResponse(Call<Player> call, Response<Player> response) {
                Log.e(TAG, "Login successful : " + response.body());

                if (callbacks != null && response.isSuccessful()) {
                    player = response.body();
                    callbacks.signInSuccessful(player);
                } else {
                    callbacks.signInFailed(getString(R.string.sign_in_error));
                }
                detach();
            }

            @Override
            public void onFailure(Call<Player> call, Throwable t) {
                Log.e(TAG, "Login failed :(", t);
                if (callbacks != null) {
                    callbacks.signInFailed(getString(R.string.sign_in_error));
                }
                detach();
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    public void setSignInCallbacks(SignInCallbacks callback) {
        callbacks = callback;
    }

    private void detach() {
        if (isAdded()) {
            getFragmentManager()
                    .beginTransaction()
                    .remove(SignInFragment.this)
                    .commitAllowingStateLoss();
        }
    }
}
