package com.wishlist.wishlist;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {
    static String serverUrl = "http://10.0.2.2:5000/";

    public static void request(String type, String subUrl, HttpResponse responseCallback) {
        new HttpRequestTask(responseCallback).execute(type, subUrl);
    }

    private static String makeRequest(String type, String subUrl) {
        StringBuffer sb = new StringBuffer();

        URL url;
        HttpURLConnection con = null;
        try {
            url = new URL(serverUrl + subUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(type);

            InputStream is = new BufferedInputStream(con.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (con != null) {
                con.disconnect();
            }
        }

        return sb.toString();
    }

    private static class HttpRequestTask extends AsyncTask<String, Void, String> {
        public HttpResponse delegate = null;

        public HttpRequestTask(HttpResponse response) {
            delegate = response;
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length > 0)
                return makeRequest(params[0], params[1]);
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            delegate.success(result);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
