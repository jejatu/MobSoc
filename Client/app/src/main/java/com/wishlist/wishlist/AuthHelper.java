package com.wishlist.wishlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jeret on 22.4.2016.
 */
public class AuthHelper {
    public static void saveAuthToken(String token, Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token);
        editor.commit();
    }

    public static String getAuthToken(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String token = pref.getString("token", "");
        return token;
    }
}
