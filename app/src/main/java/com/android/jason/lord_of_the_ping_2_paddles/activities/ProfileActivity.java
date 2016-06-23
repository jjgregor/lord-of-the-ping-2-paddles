package com.android.jason.lord_of_the_ping_2_paddles.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.jason.lord_of_the_ping_2_paddles.PingPongApplication;
import com.android.jason.lord_of_the_ping_2_paddles.R;
import com.android.jason.lord_of_the_ping_2_paddles.databinding.ActivityProfileBinding;
import com.android.jason.lord_of_the_ping_2_paddles.model.Player;
import com.android.jason.lord_of_the_ping_2_paddles.model.Profile;
import com.android.jason.lord_of_the_ping_2_paddles.viewModel.ProfileViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gregjas on 6/22/16.
 */

public class ProfileActivity extends AppCompatActivity {
    public static final String PLAYER = "player";
    private static final String TAG = ProfileActivity.class.toString();

    ProfileViewModel viewModel;
    public ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        viewModel = new ProfileViewModel(this, binding);
        binding.setModel(viewModel);
        setSupportActionBar(binding.toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.playerProfileProgressbar.setVisibility(View.VISIBLE);
        refreshData();
    }

    public void refreshData() {
        Player player = (Player) getIntent().getExtras().getSerializable(PLAYER);
        Call<Profile> p = ((PingPongApplication)getApplication()).getPingPongService().getProfile(Long.parseLong(player.getId()));
        p.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.v(TAG, "Get profile successful: " + response.raw());
                if(response.isSuccessful()){
                    Profile profile = response.body();
                    binding.playerProfileProgressbar.setVisibility(View.INVISIBLE);
                    binding.playerProfileMainContent.setVisibility(View.VISIBLE);
                    viewModel.bindProfile(profile);
                } else {
                    binding.playerProfileProgressbar.setVisibility(View.INVISIBLE);
                    binding.playerProfileEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.v(TAG, "Get profile failure: " + t.toString());
                binding.playerProfileProgressbar.setVisibility(View.INVISIBLE);
                binding.playerProfileEmpty.setVisibility(View.VISIBLE);
            }
        });

    }
}
