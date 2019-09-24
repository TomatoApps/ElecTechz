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

public class AppListAdapter extends ArrayAdapter{
    private Context context;
    private ArrayList<STORE.App> apps;

    public AppListAdapter (Context context, int resID, ArrayList<STORE.App> apps) {
        super(context, resID, apps);
        this.context = context;
        this.apps = apps;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.app_list_item, null);
        }
        else {
            listItemView = convertView;
        }

        STORE.App p = apps.get(position);

        if(p.status.equals("pending")){
            ((TextView) listItemView.findViewById(R.id.orderStatusPending)).setVisibility(View.VISIBLE);
            ((TextView) listItemView.findViewById(R.id.orderStatusCompleted)).setVisibility(View.GONE);
        }
        else if(p.status.equals("accepted")) {
            ((TextView) listItemView.findViewById(R.id.orderStatusPending)).setVisibility(View.GONE);
            ((TextView) listItemView.findViewById(R.id.orderStatusCompleted)).setVisibility(View.VISIBLE);
        }

        ((TextView)listItemView.findViewById(R.id.titleText)).setText(p.title);
        ((TextView)listItemView.findViewById(R.id.categoryText)).setText("in " + p.category);
        ((TextView)listItemView.findViewById(R.id.userEmail)).setText("from: " + p.email);
        ((TextView)listItemView.findViewById(R.id.locationText)).setText("Location: " + p.location);

        if(p.mode.equals("vacancy"))
            ((TextView)listItemView.findViewById(R.id.salaryDateText)).setText("Salary: $" + p.salaryDate);
        else
            ((TextView)listItemView.findViewById(R.id.salaryDateText)).setText("Date: " + p.salaryDate);

        return listItemView;
    }
}