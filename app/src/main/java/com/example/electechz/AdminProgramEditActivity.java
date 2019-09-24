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

public class AdminProgramEditActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progressDialog;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.programTitle) EditText programTitle;
    @BindView(R.id.programLocation) EditText programLocation;
    @BindView(R.id.programDate) EditText programDate;
    @BindView(R.id.programDetails) EditText programDetails;
    @BindView(R.id.categorySpinnerProgram) Spinner categorySpinnerProgram;
    @BindView(R.id.updateProgram) Button updateProgram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_program_edit);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateProgram.setOnClickListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.programs, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinnerProgram.setAdapter(adapter);

        programTitle.setText(getIntent().getStringExtra("title"));
        programLocation.setText(getIntent().getStringExtra("location"));
        programDate.setText(getIntent().getStringExtra("date"));
        programDetails.setText(getIntent().getStringExtra("details"));
        setSpinner(getIntent().getStringExtra("category"));
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == updateProgram.getId()) {
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

        if(valid) update();
    }

    public void update() {
        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating Program");
        progressDialog.show();

        String position = programTitle.getText().toString();
        String location = programLocation.getText().toString();
        String salary   = programDate.getText().toString();
        String details  = programDetails.getText().toString();
        String category = categorySpinnerProgram.getSelectedItem().toString();

        RetrofitRequest.
        createService(STORE.API_Client.class).
        updateProgram(getIntent().getStringExtra("id"), position, location, salary, details, category).
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
                        Toast.makeText(AdminProgramEditActivity.this, "Program Updated", Toast.LENGTH_SHORT).show();
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
            .setTitle("Delete Program")
            .setMessage("Are you sure you want to delete this program?")
            .setCancelable(false)
            .setNegativeButton("NO", null)
            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    progressDialog = new ProgressDialog(AdminProgramEditActivity.this, R.style.Theme_AppCompat_Light_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Deleting Program");
                    progressDialog.show();

                    RetrofitRequest.
                    createService(STORE.API_Client.class).
                    deleteProgram(getIntent().getStringExtra("id")).
                    enqueue(new Callback<STORE.ResponseModel>() {
                        @Override
                        public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                            progressDialog.dismiss();

                            if (response.isSuccessful()) {
                                Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                                if(response.body().getStatus() == 0) {
                                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                                } else if(response.body().getStatus() == 1) {
                                    Toast.makeText(AdminProgramEditActivity.this, "Program Deleted", Toast.LENGTH_SHORT).show();
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
            case "Orientation":
                categorySpinnerProgram.setSelection(0);
                break;
            case "Onboarding":
                categorySpinnerProgram.setSelection(1);
                break;
            case "Mandatory":
                categorySpinnerProgram.setSelection(2);
                break;
            case "Skills Development":
                categorySpinnerProgram.setSelection(3);
                break;
            case "Products and Services":
                categorySpinnerProgram.setSelection(4);
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