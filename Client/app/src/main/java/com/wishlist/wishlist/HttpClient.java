package com.wishlist.wishlist;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {
    static String serverUrl = "http://10.0.2.2:5000/";

    public static void sendGetRequest(String subUrl, HttpCallback responseCallback) {
        new HttpRequestTask(responseCallback).execute("GET", subUrl, "");
    }

    public static void sendPostRequest(String subUrl, JSONObject data, HttpCallback responseCallback) {
        new HttpRequestTask(responseCallback).execute("POST", subUrl, data.toString());
    }

    private static HttpResponse makeRequest(String type, String subUrl, String data) {
        StringBuffer sb = new StringBuffer();
        int statusCode = 0;

        URL url;
        HttpURLConnection con = null;
        try {
            url = new URL(serverUrl + subUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(1000);
            con.setRequestMethod(type);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json");

            if (!data.isEmpty()) {
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                os.write(data.getBytes("UTF-8"));
                os.close();
            }

            InputStream is = new BufferedInputStream(con.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            is.close();

            statusCode = con.getResponseCode();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (con != null) {
                con.disconnect();
            }
        }

        return new HttpResponse(statusCode, sb.toString());
    }

    private static class HttpRequestTask extends AsyncTask<String, Void, HttpResponse> {
        public HttpCallback delegate = null;

        public HttpRequestTask(HttpCallback response) {
            delegate = response;
        }

        @Override
        protected HttpResponse doInBackground(String... params) {
            if (params.length > 0)
                return makeRequest(params[0], params[1], params[2]);
            return new HttpResponse(500);
        }

        @Override
        protected void onPostExecute(HttpResponse result) {
            JSONObject json = new JSONObject();
            try {
                json = new JSONObject(result.data);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            if (result.status >= 200 && result.status < 300) {
                delegate.success(json);
            }
            else {
                delegate.failure(json);
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
