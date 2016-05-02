package com.wishlist.wishlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpClient {
    private static final int IMAGE_WIDTH = 1300;
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

    public static void sendImage(String subUrl, Bitmap image, HttpCallback responseCallback) {
        new HttpImageTask(responseCallback).execute(subUrl, image);
    }

    // copied from http://stackoverflow.com/questions/15549421/how-to-download-and-save-an-image-in-android
    public static void downloadImage(String subUrl, String name, String familyName, String productId, ImageView imageView) {
        // Used for downloading images from the server
        // Uses existing images from the phone if present
        if (name != null && familyName != null) {
            String imageName = name + "_" + familyName + "_" + productId;
            if (ImageStorage.checkifImageExists(imageName)) {
                File file = ImageStorage.getImage("/" + imageName + ".jpg");
                String path = file.getAbsolutePath();
                if (path != null) {
                    Bitmap b = BitmapFactory.decodeFile(path);
                    int width = IMAGE_WIDTH;
                    int height = (int)((float)width * ((float)b.getHeight() / (float)b.getWidth()));
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(b, width, height, false));
                }
            } else {
                new GetImages(serverUrl + subUrl, imageView, imageName).execute();
            }
        }
    }

    public static HttpResponse makeImage(String subUrl, Bitmap image) {
        // Copied mostly from http://stackoverflow.com/questions/26686806/httpurlconnection-to-send-image-audio-and-video-files-with-parameter-may-stri
        StringBuffer sb = new StringBuffer();
        int statusCode = 0;

        String fileName = "image.jpg";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        URL url;
        HttpURLConnection con = null;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            InputStream bitmapStream = new ByteArrayInputStream(stream.toByteArray());
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
            bytesAvailable = bitmapStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            // read file and write it into form...
            bytesRead = bitmapStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = bitmapStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = bitmapStream.read(buffer, 0, bufferSize);
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

    private static class HttpImageTask extends AsyncTask<Object, Void, HttpResponse> {
        public HttpCallback delegate = null;

        public HttpImageTask(HttpCallback response) {
            delegate = response;
        }

        @Override
        protected HttpResponse doInBackground(Object... params) {
            if (params.length > 0)
                return makeImage((String)params[0], (Bitmap)params[1]);
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

    // copied from http://stackoverflow.com/questions/15549421/how-to-download-and-save-an-image-in-android
    private static class GetImages extends AsyncTask<Object, Object, Object> {
        private String requestUrl, imagename_;
        private ImageView view;
        private Bitmap bitmap = null;
        private FileOutputStream fos;

        private GetImages(String requestUrl, ImageView view, String _imagename_) {
            this.requestUrl = requestUrl;
            this.view = view;
            this.imagename_ = _imagename_ ;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            try {
                URL url = new URL(requestUrl);
                URLConnection conn = url.openConnection();
                bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (bitmap != null && !ImageStorage.checkifImageExists(imagename_)) {
                int width = IMAGE_WIDTH;
                int height = (int)((float)width * ((float)bitmap.getHeight() / (float)bitmap.getWidth()));
                view.setImageBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false));
                ImageStorage.saveToSdCard(bitmap, imagename_);
            }
        }
    }
}
