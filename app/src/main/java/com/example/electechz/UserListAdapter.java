package com.example.electechz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.electechz.STORE.Vacancy;
import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter{
    private Context context;
    private ArrayList<STORE.User> users;

    public UserListAdapter(Context context, int resID, ArrayList<STORE.User> users) {
        super(context, resID, users);
        this.context = context;
        this.users = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.user_list_item, null);
        }
        else {
            listItemView = convertView;
        }

        STORE.User p = users.get(position);

        ((TextView)listItemView.findViewById(R.id.userName)).setText(p.name);
        ((TextView)listItemView.findViewById(R.id.userEmail)).setText("Email: " + p.email);
        ((TextView)listItemView.findViewById(R.id.userMobile)).setText("Mobile: " + p.mobile);
        ((TextView)listItemView.findViewById(R.id.userAddress)).setText("Address: " + p.address);

        return listItemView;
    }
}