package com.example.electechz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class ApplyActivity extends AppCompatActivity {

    Uri uri;
    boolean vacancyMode;
    String mode = "", CVfilename = "";
    ProgressDialog progressDialog;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.input_name) EditText input_name;
    @BindView(R.id.input_email) EditText input_email;
    @BindView(R.id.input_address) EditText input_address;
    @BindView(R.id.input_mobile) EditText input_mobile;
    @BindView(R.id.CVstuff) LinearLayout CVstuff;
    @BindView(R.id.fileName) TextView fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        input_name.setText(getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("name", ""));
        input_email.setText(getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("email", ""));
        input_address.setText(getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("address", ""));
        input_mobile.setText(getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("mobile", ""));

        mode = getIntent().getStringExtra("mode").toLowerCase();
        vacancyMode = getIntent().getStringExtra("mode").equals("Vacancy");

        if(!vacancyMode)
            CVstuff.setVisibility(View.GONE);
    }

    public void attach(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select File"), 123);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123) {
            try {
                uri = data.getData();
                File file = new File(URIPathLib.getPath(this, uri));
                fileName.setText(file.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void validate(View view) {
        boolean valid = true;

        String name = input_name.getText().toString();
        String email= input_email.getText().toString();
        String address = input_address.getText().toString();
        String mobile = input_mobile.getText().toString();

        if (name.isEmpty()) {
            input_name.setError("Name can't be empty");
            valid = false;
        } else {
            input_name.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_email.setError("Enter a valid email address");
            valid = false;
        } else {
            input_email.setError(null);
        }

        if (address.isEmpty()) {
            input_address.setError("Address can't be empty");
            valid = false;
        } else {
            input_address.setError(null);
        }

        if (mobile.isEmpty()) {
            input_mobile.setError("Mobile Number can't be empty");
            valid = false;
        } else {
            input_mobile.setError(null);
        }

        if(vacancyMode) {
            if(fileName.getText().equals("No File Attached")) {
                Toast.makeText(this, "No File Attached", Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }

        if(valid) apply();
    }

    public void apply() {
        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Applying");
        progressDialog.show();

        String email = input_email.getText().toString();
        String title = getIntent().getStringExtra("title");
        String location = getIntent().getStringExtra("location");
        String category = getIntent().getStringExtra("category");
        String salaryDate = getIntent().getStringExtra("salaryDate");
        if(vacancyMode) CVfilename = String.valueOf(Calendar.getInstance().getTimeInMillis());
        else CVfilename = "none";

        RetrofitRequest.
        createService(STORE.API_Client.class).
        uploadApp(mode, email, title, location, category, salaryDate, CVfilename).
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
                        if(vacancyMode) uploadCV();
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(ApplyActivity.this, "Application Sent", Toast.LENGTH_SHORT).show();
                            finish();
                            overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
                        }
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

    private void uploadCV() {
        Log.i("#####_UPLOADING", "CV");

        File file = new File(URIPathLib.getPath(this, uri));
        RequestBody reqFile = RequestBody.create(MediaType.parse("application/pdf"), file);
        MultipartBody.Part cv = MultipartBody.Part.createFormData("cv", file.getName(), reqFile);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), CVfilename);

        RetrofitRequest.
        createService(STORE.API_Client.class).
        uploadCV(cv, filename).
        enqueue(new Callback<STORE.ResponseModel>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if(response.body().getStatus() == 0) {
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == 1) {
                        Toast.makeText(ApplyActivity.this, "Application Sent", Toast.LENGTH_SHORT).show();
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
        finish();
        overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}