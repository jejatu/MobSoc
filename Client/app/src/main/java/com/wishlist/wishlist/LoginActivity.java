package com.wishlist.wishlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
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
    }

    public void createfamilygroup(View view){
        intent = new Intent(this, CreateFamilyGroup.class);
        startActivity(intent);
    }

    public void joinfamilygroup(View view){
        intent = new Intent(this, JoinFamilyGroup.class);
        startActivity(intent);
    }

    public void addTokenToSharedPreferences(String token) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token);
        editor.commit();
    }

    public void login(View view){
        intent = new Intent(this, MainActivity.class);

        EditText input_username = (EditText) findViewById(R.id.input_username);
        EditText input_password = (EditText) findViewById(R.id.input_password);
        String name = input_username.getText().toString();
        String password = input_password.getText().toString();

        HttpClient.sendPostRequest("login", JSONHelper.createLogin(name, password), new HttpCallback() {
            @Override
            public void success(JSONObject response) {
                String token = JSONHelper.parseToken(response);
                addTokenToSharedPreferences(token);
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
