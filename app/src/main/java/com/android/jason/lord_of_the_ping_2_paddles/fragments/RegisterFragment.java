package com.android.jason.lord_of_the_ping_2_paddles.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.jason.lord_of_the_ping_2_paddles.PingPongApplication;
import com.android.jason.lord_of_the_ping_2_paddles.R;
import com.android.jason.lord_of_the_ping_2_paddles.model.Player;

import java.security.MessageDigest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.android.jason.lord_of_the_ping_2_paddles.fragments.AuthFragment.sha256;

/**
 * Created by gregjas on 6/22/16.
 */

public class RegisterFragment extends Fragment {
    private static final String TAG = RegisterFragment.class.getName();
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    public static interface RegisterCallbacks {
        void registrationSuccessful(Player player);

        void registrationFailed(String error);
    }

    private RegisterCallbacks callbacks;
    private PingPongApplication app;

    public static RegisterFragment newInstance(String name, String email, String password) {
        Bundle args = new Bundle();
        args.putString(NAME, name);
        args.putString(EMAIL, email);
        args.putString(PASSWORD, password);
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        app = (PingPongApplication) getActivity().getApplication();
        String name = getArguments().getString(NAME);
        String password = getArguments().getString(PASSWORD);
        String hashedPass = sha256(password);
        String email = getArguments().getString(EMAIL);

        Log.d(TAG, "Name: " + name + " Email: " + email + " Password: " + hashedPass);
        Call<Player> player = app.getPingPongService().register(new Player(name, email, hashedPass));
        player.enqueue(new Callback<Player>() {
            @Override
            public void onResponse(Call<Player> call, Response<Player> response) {
                Log.e(TAG, "Registration successful : " + response.body() + " response code: " + response.code());

                if(callbacks != null && response.body() == null){
                    callbacks.registrationSuccessful(response.body());
                } else {
                    callbacks.registrationFailed(getString(R.string.registration_error));
                }

                detach();
            }

            @Override
            public void onFailure(Call<Player> call, Throwable t) {
                if (callbacks != null) {
                    callbacks.registrationFailed(getString(R.string.registration_error));
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

    public void setRegisterCallbacks(RegisterCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    private void detach() {
        if (isAdded()) {
            getFragmentManager()
                    .beginTransaction()
                    .remove(RegisterFragment.this)
                    .commitAllowingStateLoss();
        }
    }
}
