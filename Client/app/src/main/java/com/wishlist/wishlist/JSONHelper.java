package com.wishlist.wishlist;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jeret on 21.4.2016.
 */
public class JSONHelper {
    public static JSONObject createLogin(String name, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("password", password);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String parseToken(JSONObject response) {
        String token = "";
        try {
            token = response.getString("token");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return token;
    }
}
