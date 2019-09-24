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

public class ProgramListAdapter extends ArrayAdapter{
    private Context context;
    private ArrayList<STORE.Program> programs;

    public ProgramListAdapter(Context context, int resID, ArrayList<STORE.Program> programs) {
        super(context, resID, programs);
        this.context = context;
        this.programs = programs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.program_list_item, null);
        }
        else {
            listItemView = convertView;
        }

        STORE.Program p = programs.get(position);

        ((TextView)listItemView.findViewById(R.id.programTitle)).setText(p.title);
        ((TextView)listItemView.findViewById(R.id.programCategory)).setText("in " + p.category);
        ((TextView)listItemView.findViewById(R.id.programLocation)).setText("Location: " + p.location);
        ((TextView)listItemView.findViewById(R.id.programDate)).setText("Date: " + p.date);

        return listItemView;
    }
}