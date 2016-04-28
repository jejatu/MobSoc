package com.wishlist.wishlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MemberRequests extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_requests);
        List<Member> memberList=Member.getDummyMembers();
        ListView memberListView= (ListView) findViewById(R.id.memberRequestList);
        memberListView.setAdapter(new MemberListAdaptor(memberList));

    }
}
