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


    public static void saveProductCount(int count, Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("count", count);
        editor.commit();
    }

    public static int getProductCount(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int count = pref.getInt("count", 0);
        return count;
    }

    public static void saveInfo(String name, String familyName, Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name", name);
        editor.putString("familyName", familyName);
        editor.commit();
    }

    public static String getName(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String name = pref.getString("name", "");
        return name;
    }

    public static String getFamilyName(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String familyName = pref.getString("familyName", "");
        return familyName;
    }
}
