package com.android.jason.lord_of_the_ping_2_paddles.service;

import com.android.jason.lord_of_the_ping_2_paddles.model.LeaderboardItem;
import com.android.jason.lord_of_the_ping_2_paddles.model.Match;
import com.android.jason.lord_of_the_ping_2_paddles.model.MatchConfirmResponse;
import com.android.jason.lord_of_the_ping_2_paddles.model.Player;
import com.android.jason.lord_of_the_ping_2_paddles.model.Profile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by gregjas on 6/22/16.
 */

public interface LOTPService {

    public static String SERVER = "http://www.lordoftheping.com/";

    @GET("tt/leaderboard")
    Call<List<LeaderboardItem>> getLeaderBoard();

    @Headers("Content-Type: application/json")
    @POST("tt/signin")
    Call<Player> signIn(@Body Player credentials);

    @Headers("Content-Type: application/json")
    @POST("tt/register")
    Call<Player> register(@Body Player credentials);

    @GET("tt/profile/{id}")
    Call<Profile> getProfile(@Path("id") long playerId);

    @GET("tt/players")
    Call<List<Player>> getAllPlayers();

    @POST("tt/saveMatch")
    Call<String> saveMatch(@Body Match match);

    @GET("tt/pending/player/{id}")
    Call<List<Match>> getPendingMatches(@Path("id") long playerId);

    @POST("tt/confirmMatch")
    Call<MatchConfirmResponse> confirmMatch(@Body MatchConfirmResponse response);
}
