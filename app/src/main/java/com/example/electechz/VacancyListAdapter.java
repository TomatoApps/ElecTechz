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


public class VacancyListAdapter extends ArrayAdapter{
    private Context context;
    private ArrayList<Vacancy> vacancies;

    public VacancyListAdapter(Context context, int resID, ArrayList<Vacancy> vacancies) {
        super(context, resID, vacancies);
        this.context = context;
        this.vacancies = vacancies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.vacancy_list_item, null);
        }
        else {
            listItemView = convertView;
        }

        Vacancy p = vacancies.get(position);

        ((TextView)listItemView.findViewById(R.id.vacancyPosition)).setText(p.position);
        ((TextView)listItemView.findViewById(R.id.vacancyCategory)).setText("in " + p.category);
        ((TextView)listItemView.findViewById(R.id.vacancyLocation)).setText("Location: " + p.location);
        ((TextView)listItemView.findViewById(R.id.vacancySalary)).setText("Salary: $" + p.salary);

        return listItemView;
    }
}