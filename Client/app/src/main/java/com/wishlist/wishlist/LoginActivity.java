package com.wishlist.wishlist;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
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
        //checkIfLoggedIn(getApplicationContext());

        requestPermissions();
    }
    @Override
    public void onBackPressed() {

        //Toast.makeText(getApplicationContext(), " Back Pressed", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        finish();


    }
//    public void checkIfLoggedIn(Context context){
//        SharedPreferences sp = getApplicationContext().getSharedPreferences("loginSaved", Context.MODE_PRIVATE);
//        String username = sp.getString("username", null);
//        if(username != null ){
//            finish();
//        }
//    }

    private boolean hasPermission(String permission)
    {
        int result = getApplicationContext().checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(getApplicationContext(), "Login Onresume", Toast.LENGTH_SHORT).show();
        //checkIfLoggedIn(getApplicationContext());

    }
    @Override
    protected void onRestart() {
        super.onRestart();
       // Toast.makeText(getApplicationContext(), "Login onrestart", Toast.LENGTH_SHORT).show();
       // checkIfLoggedIn(getApplicationContext());

    }

    private void requestPermissions() {
        if (!hasPermission(Manifest.permission.INTERNET) ||
            !hasPermission(Manifest.permission.ACCESS_NETWORK_STATE) ||
            !hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            String[] permissions = new String[3];

            permissions[0] = Manifest.permission.INTERNET;
            permissions[1] = Manifest.permission.ACCESS_NETWORK_STATE;
            permissions[2] = Manifest.permission.WRITE_EXTERNAL_STORAGE;

            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_INT);
        }
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
        // This function is called upon pressing the login button
        // Sends user information to the server
        // Server gives an authentication token that this client then saves
        // The token is then used to associate the user with the api calls
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
                    AuthHelper.saveInfo(name, family_name, getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Welcome "+ name + "!", Toast.LENGTH_SHORT).show();

                    /* Saving Log in State*/

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("loginSaved", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("username", name);
                    editor.commit();
                    //------------------

                    startActivity(intent);
                }

                @Override
                public void failure(JSONObject response) {
                    Toast.makeText(getApplicationContext(), "Couldn't log in.", Toast.LENGTH_SHORT).show();
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
