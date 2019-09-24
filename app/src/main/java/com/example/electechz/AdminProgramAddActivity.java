package com.example.electechz;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class AdminProgramAddActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progressDialog;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.programTitle) EditText programTitle;
    @BindView(R.id.programLocation) EditText programLocation;
    @BindView(R.id.programDate) EditText programDate;
    @BindView(R.id.programDetails) EditText programDetails;
    @BindView(R.id.categorySpinnerProgram) Spinner categorySpinnerProgram;
    @BindView(R.id.uploadProgram) Button uploadProgram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_program_add);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        uploadProgram.setOnClickListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.programs, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinnerProgram.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == uploadProgram.getId()) {
            validateInput();
        }
    }

    public void validateInput() {
        boolean valid = true;

        String position = programTitle.getText().toString();
        String location = programLocation.getText().toString();
        String salary   = programDate.getText().toString();
        String details  = programDetails.getText().toString();

        if (position.isEmpty()) {
            programTitle.setError("Title can't be empty");
            valid = false;
        } else {
            programTitle.setError(null);
        }

        if (location.isEmpty()) {
            programLocation.setError("Location can't be empty");
            valid = false;
        } else {
            programLocation.setError(null);
        }

        if (salary.isEmpty()) {
            programDate.setError("Date can't be empty");
            valid = false;
        } else {
            programDate.setError(null);
        }

        if (details.isEmpty()) {
            programDetails.setError("Details can't be empty");
            valid = false;
        } else {
            programDetails.setError(null);
        }

        if(valid) upload();
    }

    public void upload() {
        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading Program");
        progressDialog.show();

        String position = programTitle.getText().toString();
        String location = programLocation.getText().toString();
        String salary   = programDate.getText().toString();
        String details  = programDetails.getText().toString();
        String category = categorySpinnerProgram.getSelectedItem().toString();

        RetrofitRequest.
        createService(STORE.API_Client.class).
        uploadProgram(position, location, salary, details, category).
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
                        Toast.makeText(AdminProgramAddActivity.this, "Program Uploaded", Toast.LENGTH_SHORT).show();
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