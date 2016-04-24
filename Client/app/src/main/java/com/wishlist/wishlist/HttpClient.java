package com.wishlist.wishlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {
    // http://10.0.2.2:5000/ for emulation
    // http://masu.pythonanywhere.com/ for external
    static String serverUrl = "http://masu.pythonanywhere.com/";
    static int timeout = 1000;

    public static void sendGetRequest(String subUrl, HttpCallback responseCallback) {
        new HttpRequestTask(responseCallback).execute("GET", subUrl, "");
    }

    public static void sendPostRequest(String subUrl, JSONObject data, HttpCallback responseCallback) {
        new HttpRequestTask(responseCallback).execute("POST", subUrl, data.toString());
    }

    public static void sendImage(String subUrl, String imagePath, HttpCallback responseCallback) {
        new HttpImageTask(responseCallback).execute(subUrl, imagePath);
    }

    public static HttpResponse makeImage(String subUrl, String imagePath) {
        // Copied mostly from http://stackoverflow.com/questions/26686806/httpurlconnection-to-send-image-audio-and-video-files-with-parameter-may-stri
        StringBuffer sb = new StringBuffer();
        int statusCode = 0;

        String fileName = "image.jpg";
        File sourceFile = new File(imagePath);
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        URL url;
        HttpURLConnection con = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            url = new URL(serverUrl + subUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(timeout);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("ENCTYPE", "multipart/form-data");
            con.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
            con.setRequestProperty("uploaded_file", fileName);

            DataOutputStream dos = new DataOutputStream(con.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            // create a buffer of maximum size
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

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

    private static HttpResponse makeRequest(String type, String subUrl, String data) {
        StringBuffer sb = new StringBuffer();
        int statusCode = 0;

        URL url;
        HttpURLConnection con = null;
        try {
            url = new URL(serverUrl + subUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(timeout);
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

    private static class HttpImageTask extends AsyncTask<String, Void, HttpResponse> {
        public HttpCallback delegate = null;

        public HttpImageTask(HttpCallback response) {
            delegate = response;
        }

        @Override
        protected HttpResponse doInBackground(String... params) {
            if (params.length > 0)
                return makeImage(params[0], params[1]);
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
