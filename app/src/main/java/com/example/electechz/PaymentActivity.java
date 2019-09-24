package com.example.electechz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.electechz.STORE.Product;

import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.card_form) CardForm cardForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .setup(this);
    }

    public void purchase(View view) {
        if(!cardForm.isValid())
            cardForm.validate();
        else {
            try {
                String products = "";
                SQLiteDatabase db = openOrCreateDatabase("ElecTechz", MODE_PRIVATE, null);
                Cursor c = db.rawQuery("SELECT * from cart WHERE email ='" + getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("email", "none") + "'", null);
                c.moveToFirst();

                if(c.getCount() > 0) {
                    for (int i = 0; i < c.getCount(); i++, c.moveToNext())
                        products += c.getString(c.getColumnIndex("id")) + "+" + c.getString(c.getColumnIndex("name")) + ";-;-;";

                    final ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Processing");
                    progressDialog.show();

                    String email = getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("email", "none");
                    String cc = cardForm.getCardNumber();
                    String exp = cardForm.getExpirationMonth() + "/" + cardForm.getExpirationYear();
                    String cvv = cardForm.getCvv();

                    RetrofitRequest.
                    createService(STORE.API_Client.class).
                    uploadOrder(email, cc, exp, cvv, products).
                    enqueue(new Callback<STORE.ResponseModel>() {
                        @Override
                        public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                            progressDialog.dismiss();

                            if (response.isSuccessful()) {
                                Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                                if (response.body().getStatus() == 0) {
                                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                                } else if (response.body().getStatus() == -1) {
                                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                                } else if (response.body().getStatus() == 1) {
                                    SQLiteDatabase db = openOrCreateDatabase("ElecTechz", MODE_PRIVATE, null);
                                    db.execSQL("DELETE FROM cart WHERE email = '" + getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("email", "none") + "'");
                                    db.close();

                                    Toast.makeText(PaymentActivity.this, "Purchase Order Registered", Toast.LENGTH_SHORT).show();
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
                } else {
                    Toast.makeText(this, "Cart Empty", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, CartActivity.class));
                    finish();
                    overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, CartActivity.class));
        finish();
        overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}