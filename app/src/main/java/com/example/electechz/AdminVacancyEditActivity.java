package com.example.electechz;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminVacancyEditActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progressDialog;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.vacancyPosition) EditText vacancyPosition;
    @BindView(R.id.vacancyLocation) EditText vacancyLocation;
    @BindView(R.id.vacancySalary) EditText vacancySalary;
    @BindView(R.id.vacancyDetails) EditText vacancyDetails;
    @BindView(R.id.categorySpinnerVacancy) Spinner categorySpinnerVacancy;
    @BindView(R.id.uploadVacancy) Button uploadVacancy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_vacancy_edit);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        uploadVacancy.setOnClickListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.vacancies, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinnerVacancy.setAdapter(adapter);

        vacancyPosition.setText(getIntent().getStringExtra("position"));
        vacancyLocation.setText(getIntent().getStringExtra("location"));
        vacancySalary.setText(getIntent().getStringExtra("salary"));
        vacancyDetails.setText(getIntent().getStringExtra("details"));
        setSpinner(getIntent().getStringExtra("category"));
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == uploadVacancy.getId()) {
            validateInput();
        }
    }

    public void validateInput() {
        boolean valid = true;

        String position = vacancyPosition.getText().toString();
        String location = vacancyLocation.getText().toString();
        String salary = vacancySalary.getText().toString();
        String details = vacancyDetails.getText().toString();

        if (position.isEmpty()) {
            vacancyPosition.setError("Position can't be empty");
            valid = false;
        } else {
            vacancyPosition.setError(null);
        }

        if (location.isEmpty()) {
            vacancyLocation.setError("Location can't be empty");
            valid = false;
        } else {
            vacancyLocation.setError(null);
        }

        if (salary.isEmpty()) {
            vacancySalary.setError("Salary can't be empty");
            valid = false;
        } else {
            vacancySalary.setError(null);
        }

        if (details.isEmpty()) {
            vacancyDetails.setError("Details can't be empty");
            valid = false;
        } else {
            vacancyDetails.setError(null);
        }

        if(valid) upload();
    }

    public void upload() {
        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating Vacancy");
        progressDialog.show();

        String position = vacancyPosition.getText().toString();
        String location = vacancyLocation.getText().toString();
        String salary = vacancySalary.getText().toString();
        String details = vacancyDetails.getText().toString();
        String category = categorySpinnerVacancy.getSelectedItem().toString();

        RetrofitRequest.
        createService(STORE.API_Client.class).
        updateVacancy(getIntent().getStringExtra("id"), position, location, salary, details, category).
        enqueue(new Callback<STORE.ResponseModel>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if(response.body().getStatus() == -1) {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == 0) {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == 1) {
                        progressDialog.dismiss();
                        Toast.makeText(AdminVacancyEditActivity.this, "Vacancy Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AdminListActivity.class));
                        finish();
                        overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
                    }
                } else {
                    Log.i("#####_failure", "" + response.code());
                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<STORE.ResponseModel> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "Cannot Connect to Server", Snackbar.LENGTH_LONG).show();
                Log.i("#####_HARD_FAIL", "StackTrace:");
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            new AlertDialog
            .Builder(this)
            .setTitle("Delete Vacancy")
            .setMessage("Are you sure you want to delete this vacancy?")
            .setCancelable(false)
            .setNegativeButton("NO", null)
            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    progressDialog = new ProgressDialog(AdminVacancyEditActivity.this, R.style.Theme_AppCompat_Light_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Deleting Vacancy");
                    progressDialog.show();

                    RetrofitRequest.
                    createService(STORE.API_Client.class).
                    deleteVacancy(getIntent().getStringExtra("id")).
                    enqueue(new Callback<STORE.ResponseModel>() {
                        @Override
                        public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                            progressDialog.dismiss();

                            if (response.isSuccessful()) {
                                Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                                if(response.body().getStatus() == 0) {
                                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                                } else if(response.body().getStatus() == 1) {
                                    Toast.makeText(AdminVacancyEditActivity.this, "Vacancy Deleted", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), AdminListActivity.class));
                                    finish();
                                    overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
                                }
                            } else {
                                Log.i("#####_failure", "" + response.code());
                                Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<STORE.ResponseModel> call, Throwable t) {
                            progressDialog.dismiss();
                            Snackbar.make(findViewById(android.R.id.content), "Cannot Connect to Server", Snackbar.LENGTH_LONG).show();
                            Log.i("#####_HARD_FAIL", "StackTrace:");
                            t.printStackTrace();
                        }
                    });
                }})
            .show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setSpinner(String category) {
        switch (category) {
            case "Manager":
                categorySpinnerVacancy.setSelection(0);
                break;
            case "Technician":
                categorySpinnerVacancy.setSelection(1);
                break;
            case "Engineer":
                categorySpinnerVacancy.setSelection(2);
                break;
            case "Analyst":
                categorySpinnerVacancy.setSelection(3);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), AdminListActivity.class));
        finish();
        overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}