package com.example.electechz;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.input_email) EditText input_email;
    @BindView(R.id.input_password) EditText input_password;
    @BindView(R.id.input_reEnterPassword) EditText input_reEnterPassword;
    @BindView(R.id.input_name) EditText input_name;
    @BindView(R.id.input_address) EditText input_address;
    @BindView(R.id.input_mobile) EditText input_mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String mode = getIntent().getStringExtra("mode");

        input_email.setEnabled(false);
        input_email.setText(getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("email", ""));
        input_name.setText(getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("name", ""));
        input_address.setText(getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("address", ""));
        input_mobile.setText(getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("mobile", ""));
    }

    public void validate(View view) {
        boolean valid = true;

        String name = input_name.getText().toString();
        String address = input_address.getText().toString();
        String mobile = input_mobile.getText().toString();
        String password = input_password.getText().toString();
        String reEnterPassword = input_reEnterPassword.getText().toString();

        if (!(reEnterPassword.equals(password))) {
            input_password.setError("Passwords do not match");
            input_reEnterPassword.setError("Passwords do not match");
            valid = false;
        } else {
            input_password.setError(null);
            input_reEnterPassword.setError(null);
        }

        if (name.isEmpty()) {
            input_name.setError("Name can't be empty");
            valid = false;
        } else {
            input_name.setError(null);
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

        if(valid) update();
    }

    public void update() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating");
        progressDialog.show();

        final int type = getSharedPreferences(STORE.SP, MODE_PRIVATE).getInt("type", -1);
        final String email = input_email.getText().toString();
        final String password = input_password.getText().toString().isEmpty() ? null : input_password.getText().toString();
        final String name = input_name.getText().toString();
        final String address = input_address.getText().toString();
        final String mobile = input_mobile.getText().toString();

        RetrofitRequest.
        createService(STORE.API_Client.class).
        updateUser(type, email, password, name, address, mobile).
        enqueue(new Callback<STORE.ResponseModel>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if(response.body().getStatus() == -1) {
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == 0) {
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == 1) {
                        SharedPreferences.Editor editor = getSharedPreferences(STORE.SP, MODE_PRIVATE).edit();
                        editor.putString("name", name);
                        editor.putString("address", address);
                        editor.putString("mobile", mobile);
                        editor.apply();

                        new SendMail(email, "Profile Updated", "Hello, " + name + "\n\nYour profile info has been updated.");
                        Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
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