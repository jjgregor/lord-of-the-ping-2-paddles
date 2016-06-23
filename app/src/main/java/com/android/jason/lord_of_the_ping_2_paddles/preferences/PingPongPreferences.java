package com.android.jason.lord_of_the_ping_2_paddles.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.jason.lord_of_the_ping_2_paddles.model.Player;
import com.android.jason.lord_of_the_ping_2_paddles.util.ObjectMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * Created by gregjas on 6/22/16.
 */

public class PingPongPreferences {

    public static final String SHARED_PREFS = "ping_pong_shared_prefs";

    private static final String CURRENT_PLAYER = "current_player";

    public static Player getCurrentPlayer(Context context) {
        String player = getSharedPreferences(context).getString(CURRENT_PLAYER, null);
        if (StringUtils.isNotEmpty(player)) {
            try {
                return ObjectMapperFactory.getObjectMapper().readValue(player, Player.class);
            } catch (IOException e) {
                // TODO do something
            }
        }
        return null;
    }

    public static boolean setCurrentPlayer(Player player, Context context) {
        try {
            getSharedPreferences(context)
                    .edit()
                    .putString(CURRENT_PLAYER, ObjectMapperFactory.getObjectMapper().writeValueAsString(player))
                    .apply();
            return true;
        } catch (JsonProcessingException e) {
            // TODO log or something
            return false;
        }
    }

    public static void signOut(Context context) {
        getSharedPreferences(context).edit().remove(CURRENT_PLAYER).apply();
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }
}
