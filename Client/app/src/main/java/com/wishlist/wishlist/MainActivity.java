package com.wishlist.wishlist;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_INT = 13;
    TextView debugText;
    Button debugButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //test commit
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        debugText = (TextView) findViewById(R.id.debugText);
        debugButton = (Button) findViewById(R.id.debugButton);
        debugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpClient.request("GET", "test", new HttpResponse() {
                    @Override
                    public void success(String response) {
                        String formattedResponse = parseTestResponse(response);
                        debugText.setText(formattedResponse);
                    }
                });
            }
        });

        requestPermissions();
    }

    public String parseTestResponse(String response) {
        String formattedResponse = "";
        try {
            JSONObject json = new JSONObject(response);
            Iterator<String> iter = json.keys();

            while (iter.hasNext()) {
                String key = iter.next();
                String value = json.getString(key);
                formattedResponse += key + ": " + value + "\n";
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return formattedResponse;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestPermissions() {
        String[] permissions = new String[2];

        permissions[0] = Manifest.permission.INTERNET;
        permissions[1] = Manifest.permission.ACCESS_NETWORK_STATE;

        ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_INT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_INT: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Wishlist", "Permission " + i + " granted!");
                    }
                    else {
                        Log.d("Wishlist", "Permission " + i + " denied...");
                    }
                }
            }
        }
    }
}
