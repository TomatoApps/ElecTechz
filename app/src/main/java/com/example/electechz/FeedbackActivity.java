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
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.input_feedback) EditText input_feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void sendFeedback(View view) {
        if(input_feedback.getText().toString().isEmpty()){
            input_feedback.setError("Feedback can't be empty");
        } else {
            input_feedback.setError(null);

            final ProgressDialog progressDialog;
            progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Sending");
            progressDialog.show();

            final int type = getSharedPreferences(STORE.SP, MODE_PRIVATE).getInt("type", -1);
            final String email = getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("email", "none");
            final String feedback = input_feedback.getText().toString();

            RetrofitRequest.
            createService(STORE.API_Client.class).
            uploadFeedback(type, email, feedback).
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
                            Toast.makeText(FeedbackActivity.this, "Thank you for your feedback", Toast.LENGTH_SHORT).show();
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
