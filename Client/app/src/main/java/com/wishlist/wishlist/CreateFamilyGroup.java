package com.wishlist.wishlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class CreateFamilyGroup extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_family_group);
    }

    public void login(View view){
        intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void joinfamilygroup(View view){
        intent = new Intent(this, JoinFamilyGroup.class);
        startActivity(intent);
    }

    public void createfamilygroup(View view){
        intent = new Intent(this, MainActivity.class);

        EditText input_username = (EditText) findViewById(R.id.input_username);
        EditText input_email = (EditText) findViewById(R.id.input_email);
        EditText input_familyname = (EditText) findViewById(R.id.input_familyname);
        EditText input_password = (EditText) findViewById(R.id.input_password);
        final String name = input_username.getText().toString();
        final String email = input_email.getText().toString();
        final String familyName = input_familyname.getText().toString();
        final String password = input_password.getText().toString();

        HttpClient.sendPostRequest("register_family", JSONHelper.createRegisterFamily(name, email, familyName, password), new HttpCallback() {
            @Override
            public void success(JSONObject response) {
                HttpClient.sendPostRequest("login", JSONHelper.createLogin(name, familyName, password), new HttpCallback() {
                    @Override
                    public void success(JSONObject response) {
                        String token = JSONHelper.parseToken(response);
                        AuthHelper.saveAuthToken(token, getApplicationContext());
                        AuthHelper.saveInfo(name, familyName, getApplicationContext());
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

            @Override
            public void failure(JSONObject response) {
                Toast.makeText(getApplicationContext(), "Registeration failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
