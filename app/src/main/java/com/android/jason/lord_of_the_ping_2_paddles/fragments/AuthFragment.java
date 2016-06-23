package com.android.jason.lord_of_the_ping_2_paddles.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.jason.lord_of_the_ping_2_paddles.PingPongApplication;
import com.android.jason.lord_of_the_ping_2_paddles.R;
import com.android.jason.lord_of_the_ping_2_paddles.model.Player;
import com.android.jason.lord_of_the_ping_2_paddles.preferences.PingPongPreferences;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.security.MessageDigest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gregjas on 6/22/16.
 */

public class AuthFragment extends Fragment implements SignInFragment.SignInCallbacks, RegisterFragment.RegisterCallbacks {
    private static final String STATE_REGISTER_VISIBLE = "register_visible";
    public static final String TAG = AuthFragment.class.getName();

    @BindView(R.id.sign_in_btn)
    Button btnSignIn;

    @BindView(R.id.sign_in_email)
    EditText signInEmail;

    @BindView(R.id.sign_in_password)
    EditText signInPassword;

    @BindView(R.id.register_btn)
    Button btnRegister;

    @BindView(R.id.register_email)
    EditText registerEmail;

    @BindView(R.id.register_name)
    EditText registerName;

    @BindView(R.id.register_password)
    EditText registerPassword;

    @BindView(R.id.auth_sign_in_cont)
    ViewGroup signInCont;

    @BindView(R.id.auth_register_cont)
    ViewGroup registerCont;

    @BindView(R.id.auth_failure_cont)
    ViewGroup failureCont;

    @BindView(R.id.auth_try_again_btn)
    Button tryAgainBtn;

    @BindView(R.id.sign_in_register_btn)
    Button btnSignInRegister;

    @BindView(R.id.register_sign_in_btn)
    Button btnRegisterSignIn;

    @BindView(R.id.auth_doing_stuff_cont)
    LinearLayout doingStuffCont;

    @BindView(R.id.auth_failure_lbl)
    TextView failureLbl;

    private AuthCallbacks callBacks;
    private PingPongApplication app;

    public static interface AuthCallbacks {
        void playerSignedIn(Player player);

        void authCancelled();
    }

    public void setAuthCallbacks(AuthCallbacks callback) {
        callBacks = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_auth, container, false);
        ButterKnife.bind(this, root);
        addListeners();
        app = (PingPongApplication) getActivity().getApplication();

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            boolean registerVisible = savedInstanceState.getBoolean(STATE_REGISTER_VISIBLE, false);
            if (registerVisible) {
                signInCont.setVisibility(View.GONE);
                registerCont.setVisibility(View.VISIBLE);
            }
        }
    }

    private void addListeners() {
        btnSignInRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInCont
                        .animate()
                        .alpha(0)
                        .setListener(
                                new AnimatorListenerAdapter() {

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        registerCont.setAlpha(0);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        signInCont.setVisibility(View.GONE);
                                        registerCont.setVisibility(View.VISIBLE);
                                        registerCont.animate()
                                                .alpha(1)
                                                .setListener(null)
                                                .start();
                                    }
                                }
                        )
                        .start();
            }
        });

        btnRegisterSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCont
                        .animate()
                        .alpha(0)
                        .setListener(
                                new AnimatorListenerAdapter() {

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        signInCont.setAlpha(0);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        registerCont.setVisibility(View.GONE);
                                        signInCont.setVisibility(View.VISIBLE);
                                        signInCont.animate()
                                                .alpha(1)
                                                .setListener(null)
                                                .start();
                                    }
                                }
                        )
                        .start();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_REGISTER_VISIBLE, registerCont != null && View.VISIBLE == registerCont.getVisibility());
    }

    @OnClick(R.id.sign_in_btn)
    public void onSignInClicked() {
        dismissKeyboard();
        final String email = signInEmail.getText().toString();
        final String password = signInPassword.getText().toString();
        if (StringUtils.isEmpty(email)) {
            signInEmail.setError(getString(R.string.auth_email_blank));
            return;
        } else {
            signInEmail.setError(null);
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            signInEmail.setError(getString(R.string.auth_email_invalid));
            return;
        } else {
            signInEmail.setError(null);
        }
        if (StringUtils.isEmpty(password)) {
            signInPassword.setError(getString(R.string.auth_password_invalid));
            return;
        } else {
            signInPassword.setError(null);
        }
        SignInFragment fragment = SignInFragment.newInstance(email, password);
        fragment.setSignInCallbacks(AuthFragment.this);
        getFragmentManager()
                .beginTransaction()
                .add(fragment, SignInFragment.class.getName())
                .commit();
    }

    @OnClick(R.id.register_btn)
    public void onRegisterClicked() {
        dismissKeyboard();
        final String name = registerName.getText().toString();
        final String email = registerEmail.getText().toString();
        final String password = registerPassword.getText().toString();
        if (StringUtils.isEmpty(name)) {
            registerName.setError(getString(R.string.auth_name_invalid));
            return;
        } else {
            registerName.setError(null);
        }
        if (StringUtils.isEmpty(email)) {
            registerEmail.setError(getString(R.string.auth_email_blank));
            return;
        } else {
            registerEmail.setError(null);
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            registerEmail.setError(getString(R.string.auth_email_invalid));
            return;
        } else {
            registerEmail.setError(null);
        }
        if (StringUtils.isEmpty(password)) {
            registerPassword.setError(getString(R.string.auth_password_invalid));
            return;
        } else {
            registerPassword.setError(null);
        }

        RegisterFragment fragment = RegisterFragment.newInstance(
                registerName.getText().toString(),
                registerEmail.getText().toString(),
                registerPassword.getText().toString()
        );

        fragment.setRegisterCallbacks(AuthFragment.this);
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_left_exit)
                .add(fragment, RegisterFragment.class.getName())
                .commit();
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(signInEmail.getWindowToken(), 0);
    }

    @Override
    public void signInSuccessful(Player player) {
        if (player != null) {
            playerSignedIn(player);
        }
    }

    private void playerSignedIn(Player player) {
        PingPongPreferences.setCurrentPlayer(player, getActivity());
        app.setCurrentPlayer(player);

        ProfileFragment profileFragment = ProfileFragment.newInstance(player);
        getFragmentManager()
                .beginTransaction()
                .remove(this)
                .add(profileFragment, "profile_fragment")
                .commit();
        if (callBacks != null) {
            callBacks.playerSignedIn(player);
        }
        if (getView() != null) {
            Snackbar.make(getView(), getString(R.string.sign_in_successful), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void signInFailed(String error) {
        authFailed(false, error);
        if (getView() != null) {
            Snackbar.make(getView(), getString(R.string.sign_in_error), Snackbar.LENGTH_LONG).show();
        }
    }

    private void authFailed(final boolean isRegistration, final String error) {
        if (isRegistration) {
            btnRegister.setText("Try Again");
        } else {
            btnSignIn.setText("Try Again");
        }

    }

    @Override
    public void registrationSuccessful(final Player player) {
        if (player != null) {
            playerSignedIn(player);
        } else {
            if (getView() != null) {
                Snackbar.make(getView(), "Sorry we couldn't register you in at this time", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void registrationFailed(String error) {
        authFailed(true, error);
        if (getView() != null) {
            Snackbar.make(getView(), getString(R.string.registration_error), Snackbar.LENGTH_LONG).show();
        }
    }

    public static String sha256(String input) {
        String result = input;
        if (input != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(input.getBytes("UTF-8"));
                byte[] digest = md.digest();
                result = String.format("%064x", new java.math.BigInteger(1, digest));
            } catch (Exception e) {
                System.out.println("Error hashing password: " + e);
            }
        }
        return result;
    }
}
