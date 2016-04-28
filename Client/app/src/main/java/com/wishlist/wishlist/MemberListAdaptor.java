package com.wishlist.wishlist;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by honey on 4/20/2016.
 */
public class MemberListAdaptor implements android.widget.ListAdapter {
    private List<Member> memberList;
    public MemberListAdaptor(List<Member> memberList){
        this.memberList=memberList;
    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public Object getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static class ViewHolder {
        public TextView rowMemberName;
        public Button rowAddButton;
        public Button rowDelButton;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            final Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.memer_row, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.rowMemberName = (TextView)convertView.findViewById(R.id.memberName);
            viewHolder.rowAddButton =(Button) convertView.findViewById(R.id.addMember);
            viewHolder.rowDelButton =(Button) convertView.findViewById(R.id.deleteMember);
            final int memberPosition = position;
            viewHolder.rowAddButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String id = memberList.get(memberPosition).getMemberId();
                    if (id != null) {
                        String token = AuthHelper.getAuthToken(context);
                        HttpClient.sendPostRequest("members?token=" + token, JSONHelper.createAddMember(id), new HttpCallback() {
                            @Override
                            public void success(JSONObject response) {
                                memberList.remove(memberPosition);
                                Activity activity = (context instanceof Activity) ? (Activity) context : null;
                                if (activity != null) {
                                    ListView memberListView = (ListView) activity.findViewById(R.id.memberRequestList);
                                    memberListView.setAdapter(new MemberListAdaptor(memberList));
                                }
                            }


                            @Override
                            public void failure(JSONObject response) {}
                        });
                    }
                }
            });

            viewHolder.rowDelButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String id = memberList.get(memberPosition).getMemberId();
                    if (id != null) {
                        String token = AuthHelper.getAuthToken(context);
                        HttpClient.sendPostRequest("members?token=" + token, JSONHelper.createDeleteMember(id), new HttpCallback() {
                            @Override
                            public void success(JSONObject response) {
                                memberList.remove(memberPosition);
                                Activity activity = (context instanceof Activity) ? (Activity) context : null;
                                if (activity != null) {
                                    ListView memberListView = (ListView) activity.findViewById(R.id.memberRequestList);
                                    memberListView.setAdapter(new MemberListAdaptor(memberList));
                                }
                            }

                            @Override
                            public void failure(JSONObject response) {}
                        });
                    }
                }
            });
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder)convertView.getTag();

        viewHolder.rowMemberName.setText(memberList.get(position).getMemberName());

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
