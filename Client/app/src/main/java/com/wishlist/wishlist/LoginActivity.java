package com.wishlist.wishlist;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_INT = 13;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        requestPermissions();
    }

    private void requestPermissions() {
        String[] permissions = new String[3];

        permissions[0] = Manifest.permission.INTERNET;
        permissions[1] = Manifest.permission.ACCESS_NETWORK_STATE;
        permissions[2] = Manifest.permission.WRITE_EXTERNAL_STORAGE;

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

    public void createfamilygroup(View view) {
        intent = new Intent(this, CreateFamilyGroup.class);
        startActivity(intent);
    }

    public void joinfamilygroup(View view) {
        intent = new Intent(this, JoinFamilyGroup.class);
        startActivity(intent);
    }

    public void login(View view) {
        intent = new Intent(this, MainActivity.class);

        EditText input_username = (EditText) findViewById(R.id.input_username);
        EditText input_password = (EditText) findViewById(R.id.input_password);
        final String username = input_username.getText().toString();
        final String password = input_password.getText().toString();

        String[] names = username.split("@");

        if (names.length == 2) {
            final String name = names[0];
            final String family_name = names[1];
            HttpClient.sendPostRequest("login", JSONHelper.createLogin(name, family_name, password), new HttpCallback() {
                @Override
                public void success(JSONObject response) {
                    String token = JSONHelper.parseToken(response);
                    AuthHelper.saveAuthToken(token, getApplicationContext());
                    AuthHelper.saveInfo(name, family_name);
                    Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }

                @Override
                public void failure(JSONObject response) {
                    Toast.makeText(getApplicationContext(), "Login failed... continuing for debugging purposes.", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Not a valid username.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
