package com.wishlist.wishlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CreateFamilyGroup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_family_group);

    }
    public void login(View view){
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void joinfamilygroup(View view){
        Intent intent=new Intent(this, JoinFamilyGroup.class);
        startActivity(intent);
    }
    public void createfamilygroup(View view){
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
