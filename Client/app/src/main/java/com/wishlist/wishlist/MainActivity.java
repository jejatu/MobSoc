package com.wishlist.wishlist;

import android.Manifest;
import android.content.pm.PackageManager;
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_INT = 13;
    TextView debugText;
    Button debugButton;
    String serverUrl = "localhost:5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                testRequest();
            }
        });

        requestPermissions();
    }

    public void testRequest() {
        URL url;
        HttpURLConnection con = null;
        try {
            url = new URL(serverUrl + "/test");
            con = (HttpURLConnection) url.openConnection();

            InputStream in = con.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);

            String response = "";
            int data = reader.read();
            while (data != -1) {
                char c = (char) data;
                response += c;
                data = reader.read();
            }
            debugText.setText(response);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (con != null) {
                con.disconnect();
            }
        }
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
        String[] permissions = new String[3];

        permissions[0] = Manifest.permission.INTERNET;

        ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_INT);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_INT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Wishlist", "Permissions granted!");
                }
                else {
                    Log.d("Wishlist", "Permissions denied...");
                }
                return;
            }
        }
    }
}
