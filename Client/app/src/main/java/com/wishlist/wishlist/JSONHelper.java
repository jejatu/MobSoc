package com.wishlist.wishlist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jeret on 21.4.2016.
 */
public class JSONHelper {
    public static JSONObject createLogin(String name, String family_name, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("family_name", family_name);
            json.put("password", password);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static JSONObject createAddProduct(String token, String name, String description) {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("description", description);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static JSONObject createRegisterFamily(String name, String email, String familyName, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("email", email);
            json.put("family_name", familyName);
            json.put("password", password);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static JSONObject createRegisterMember(String name, String familyName, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("family_name", familyName);
            json.put("password", password);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String parseProductId(JSONObject response) {
        String id = "";
        try {
            id = response.getString("product_id");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
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

    public static List<Product> parseProducts(JSONObject response) {
        List<Product> productList = new ArrayList();
        try {
            JSONArray products = response.getJSONArray("products");
            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                String name = product.getString("name");
                String description = product.getString("description");
                String adder = product.getString("adder");
                String add_date = product.getString("add_date");
                String product_id = product.getString("product_id");
                String has_image = product.getString("has_image");

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
                Date date = new Date();

                try {
                    date = format.parse(add_date);
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }

                boolean hasImage = false;
                if (has_image.equals("1")) {
                    hasImage = true;
                }
                productList.add(new Product(name, description, adder, product_id, date, hasImage, true));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return productList;
    }
}
