package com.example.electechz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.input_email) EditText input_email;
    @BindView(R.id.input_password) EditText input_password;
    @BindView(R.id.typeToggle) ToggleSwitch typeToggle;
    @BindView(R.id.btn_login) Button btn_login;
    @BindView(R.id.link_register) TextView link_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        btn_login.setOnClickListener(this);
        link_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == link_register.getId()) {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            finish();
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        } else if (view.getId() == btn_login.getId())
            validate();
    }

    public void validate() {
        boolean valid = true;

        String email = input_email.getText().toString();
        String password = input_password.getText().toString();

        if(email.toLowerCase().equals("admin"))
            input_email.setError(null);
        else {
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                input_email.setError("Enter a valid email address");
                valid = false;
            } else {
                input_email.setError(null);
            }
        }

        if (password.isEmpty()) {
            input_password.setError("Password can't be empty");
            valid = false;
        } else {
            input_password.setError(null);
        }

        if(valid) login();
    }

    public void login() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Verifying");
        progressDialog.show();

        int type = typeToggle.getCheckedTogglePosition();
        final String email = input_email.getText().toString();
        final String password = input_password.getText().toString();

        if(email.toLowerCase().equals("admin"))
            type = -1;

        RetrofitRequest.
        createService(STORE.API_Client.class).
        login(type, email, password).
        enqueue(new Callback<STORE.ResponseModel>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if(response.body().getStatus() == -1) {
                        Snackbar.make(findViewById(android.R.id.content), "Incorrect Credentials", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == 0) {
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == 1) {
                        logInUser(response.body().getType(), email, response.body().getName(), response.body().getAddress(), response.body().getMobile());
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

    public void logInUser(int type, String email, String name, String address, String mobile) {
        SharedPreferences.Editor editor = getSharedPreferences(STORE.SP, MODE_PRIVATE).edit();
        editor.putString("exists", "YES");
        editor.putString("email", email);
        editor.putString("name", name);
        editor.putString("address", address);
        editor.putString("mobile", mobile);
        editor.putInt("type", type);
        editor.apply();

        if(type == -1)
            startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
        else
            startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));

        finish();
        overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
    }
}