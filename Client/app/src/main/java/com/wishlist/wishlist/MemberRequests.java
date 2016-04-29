package com.wishlist.wishlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MemberRequests extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_requests);
        refreshMembers();
    }

    private void refreshMembers() {
        final ListView memberListView = (ListView) findViewById(R.id.memberRequestList);
        String token = AuthHelper.getAuthToken(getApplicationContext());
        HttpClient.sendGetRequest("members?token=" + token, new HttpCallback() {
            @Override
            public void success(JSONObject response) {
                List<Member> memberList = JSONHelper.parseMembers(response);
                memberListView.setAdapter(new MemberListAdaptor(memberList));

            }

            @Override
            public void failure(JSONObject response) {
                memberListView.setAdapter(new MemberListAdaptor(Member.getDummyMembers()));
            }
        });
    }
}
