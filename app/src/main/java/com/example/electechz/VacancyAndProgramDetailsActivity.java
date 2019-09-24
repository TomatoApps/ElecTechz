package com.example.electechz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VacancyAndProgramDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.titleText) TextView titleText;
    @BindView(R.id.categoryText) TextView categoryText;
    @BindView(R.id.locationText) TextView locationText;
    @BindView(R.id.salaryDateText) TextView salaryDateText;
    @BindView(R.id.descriptionText) TextView descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacancy_and_program_details);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        titleText.setText(getIntent().getStringExtra("title"));
        categoryText.setText("in " + getIntent().getStringExtra("category"));
        locationText.setText("Location: "+ getIntent().getStringExtra("location"));
        descriptionText.setText(getIntent().getStringExtra("details"));

        if(getIntent().getStringExtra("mode").equals("Vacancy"))
            salaryDateText.setText("Salary: $" + getIntent().getStringExtra("salaryDate"));
        else
            salaryDateText.setText("Date: " + getIntent().getStringExtra("salaryDate"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vacancy_program_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_apply) {
            Intent i = new Intent(getApplicationContext(), ApplyActivity.class);
            i.putExtra("mode", getIntent().getStringExtra("mode"));
            i.putExtra("title", getIntent().getStringExtra("title"));
            i.putExtra("location", getIntent().getStringExtra("location"));
            i.putExtra("category", getIntent().getStringExtra("category"));
            i.putExtra("salaryDate", getIntent().getStringExtra("salaryDate"));
            startActivity(i);
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}