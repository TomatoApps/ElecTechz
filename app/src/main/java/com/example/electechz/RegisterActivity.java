package com.example.electechz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.net.URL;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.contentContainer) ScrollView contentContainer;
    @BindView(R.id.input_email) EditText input_email;
    @BindView(R.id.input_password) EditText input_password;
    @BindView(R.id.input_reEnterPassword) EditText input_reEnterPassword;
    @BindView(R.id.input_name) EditText input_name;
    @BindView(R.id.input_address) EditText input_address;
    @BindView(R.id.input_mobile) EditText input_mobile;
    @BindView(R.id.typeToggle) ToggleSwitch typeToggle;
    @BindView(R.id.btn_signup) Button btn_signup;
    @BindView(R.id.link_login) TextView link_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        btn_signup.setOnClickListener(this);
        link_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == link_login.getId()) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
        } else if (view.getId() == btn_signup.getId())
            validate();
    }

    public void validate() {
        boolean valid = true;

        String email = input_email.getText().toString();
        String password = input_password.getText().toString();
        String reEnterPassword = input_reEnterPassword.getText().toString();
        String name = input_name.getText().toString();
        String address = input_address.getText().toString();
        String mobile = input_mobile.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_email.setError("Enter a valid email address");
            focusOnView(contentContainer, input_email);
            valid = false;
        } else {
            input_email.setError(null);
        }

        if (password.isEmpty()) {
            input_password.setError("Password can't be empty");
            focusOnView(contentContainer, input_password);
            valid = false;
        } else {
            input_password.setError(null);
        }

        if (!(reEnterPassword.equals(password))) {
            input_reEnterPassword.setError("Passwords do not match");
            valid = false;
        } else {
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
            input_mobile.setError("Mobile number can't be empty");
            valid = false;
        } else {
            input_mobile.setError(null);
        }

        if(valid) register();
    }

    public void register() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Creating Account");
        progressDialog.show();

        final int type = typeToggle.getCheckedTogglePosition();
        final String email = input_email.getText().toString();
        final String password = input_password.getText().toString();
        final String name = input_name.getText().toString();
        final String address = input_address.getText().toString();
        final String mobile = input_mobile.getText().toString();

        RetrofitRequest.
        createService(STORE.API_Client.class).
        register(type, email, password, name, address, mobile).
        enqueue(new Callback<STORE.ResponseModel>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if(response.body().getStatus() == -1) {
                        String selectedtype = "";
                        int type = typeToggle.getCheckedTogglePosition();

                        if(type == 0) selectedtype = "a customer";
                        else if(type == 1) selectedtype = "an employee";
                        else if(type == 2) selectedtype = "a trainee";

                        input_email.setError("'" + input_email.getText().toString() + "' is already registered as " + selectedtype);
                        focusOnView(contentContainer, input_email);
                    } else if(response.body().getStatus() == 0) {
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == 1) {
                        new SendMail(email, "Account Registered", "Hello, " + name + "! \n\nYour account has been registered on ElecTechz.");
                        logInUser(type, email, name, address, mobile);
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

    private void focusOnView(ScrollView scroll, EditText et) {
        scroll.smoothScrollTo(((et.getLeft() + et.getRight() - scroll.getWidth()) / 2), 0);
        et.requestFocus();
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

        startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
        finish();
        overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
    }
}