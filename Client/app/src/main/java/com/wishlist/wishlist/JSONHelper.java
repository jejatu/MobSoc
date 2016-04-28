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
        }
        return json;
    }

    public static JSONObject createLogout(String token) {
        JSONObject json = new JSONObject();
        try {
            json.put("token", token);
        }
        catch (JSONException e) {
        }
        return json;
    }

    public static JSONObject createAddProduct(String name, String description) {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("description", description);
        }
        catch (JSONException e) {
        }
        return json;
    }

    public static JSONObject createPurchase(String product_id) {
        JSONObject json = new JSONObject();
        try {
            json.put("product_id", product_id);
        }
        catch (JSONException e) {
        }
        return json;
    }

    public static JSONObject createAddMember(String member_id) {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", member_id);
            json.put("action", "add");
        }
        catch (JSONException e) {
        }
        return json;
    }

    public static JSONObject createDeleteMember(String member_id) {
        JSONObject json = new JSONObject();
        try {
            json.put("member_id", member_id);
            json.put("action", "delete");
        }
        catch (JSONException e) {
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
        }
        return json;
    }

    public static String parseProductId(JSONObject response) {
        String id = "";
        try {
            id = response.getString("product_id");
        }
        catch (JSONException e) {
        }
        return id;
    }

    public static String parseToken(JSONObject response) {
        String token = "";
        try {
            token = response.getString("token");
        }
        catch (JSONException e) {
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
                String status = product.getString("status");

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

                boolean purchased = false;
                if (status.equals("1")) {
                    purchased = true;
                }
                productList.add(new Product(name, description, adder, product_id, date, hasImage, purchased));
            }
        }
        catch (JSONException e) {
        }
        return productList;
    }

    public static List<Member> parseMembers(JSONObject response) {
        List<Member> list = new ArrayList();
        try {
            JSONArray members = response.getJSONArray("members");
            for (int i = 0; i < members.length(); i++) {
                JSONObject product = members.getJSONObject(i);
                String user_id = product.getString("user_id");
                String name = product.getString("name");
                String activated = product.getString("activated");

                if (activated.equals("0")) {
                    list.add(new Member(name, user_id));
                }
            }
        }
        catch (JSONException e) {
        }
        return list;
    }
}
