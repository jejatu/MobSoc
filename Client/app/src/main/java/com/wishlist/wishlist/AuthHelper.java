package com.wishlist.wishlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jeret on 22.4.2016.
 */
public class AuthHelper {
    private static String name_;
    private static String familyName_;

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

    public static void saveInfo(String name, String familyName) {
        name_ = name;
        familyName_ = familyName;
    }

    public static String getName() {
        return name_;
    }

    public static String getFamilyName() {
        return familyName_;
    }
}
