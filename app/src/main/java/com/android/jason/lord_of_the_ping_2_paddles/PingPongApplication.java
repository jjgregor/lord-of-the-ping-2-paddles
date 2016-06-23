package com.android.jason.lord_of_the_ping_2_paddles;

import android.app.Application;
import android.util.Log;

import com.android.jason.lord_of_the_ping_2_paddles.model.Player;
import com.android.jason.lord_of_the_ping_2_paddles.preferences.PingPongPreferences;
import com.android.jason.lord_of_the_ping_2_paddles.service.LOTPService;
import com.android.jason.lord_of_the_ping_2_paddles.util.ObjectMapperFactory;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by gregjas on 6/22/16.
 */

public class PingPongApplication extends Application {

    private LOTPService service;
    private Player mCurrentPlayer;
    public static final String TAG = PingPongApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(LOTPService.SERVER)
                .addConverterFactory(JacksonConverterFactory.create(ObjectMapperFactory.getObjectMapper()))
                .client(client)
                .build();

        service = retrofit.create(LOTPService.class);
        mCurrentPlayer = PingPongPreferences.getCurrentPlayer(this);
        Log.d(TAG, "CurrentPlayer: " + mCurrentPlayer);

        Stetho.initializeWithDefaults(this);
    }

    public LOTPService getPingPongService() {
        return service;
    }

    public Player getCurrentPlayer() {
        return mCurrentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        mCurrentPlayer = currentPlayer;
    }


}
