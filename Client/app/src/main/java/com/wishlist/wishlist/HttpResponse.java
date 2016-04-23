package com.wishlist.wishlist;

public class HttpResponse {
    int status = 0;
    String data = "";

    public HttpResponse(int status, String data) {
        this.status = status;
        this.data = data;
    }

    public HttpResponse(int status) {
        this.status = status;
        this.data = "";
    }
}
