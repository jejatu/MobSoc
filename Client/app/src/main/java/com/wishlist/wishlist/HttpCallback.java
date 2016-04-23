package com.wishlist.wishlist;

import org.json.JSONObject;

public interface HttpCallback {
    void success(JSONObject response);
    void failure(JSONObject response);
}
