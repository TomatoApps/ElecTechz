package com.example.electechz;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.electechz.STORE.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String email = null, mode = "";
    ArrayList<Order> orders;
    OrderListAdapter adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.loader) ProgressBar loader;
    @BindView(R.id.nothingFound) TextView nothingFound;
    @BindView(R.id.listView) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mode = getIntent().getStringExtra("mode");
        if(mode.equals("user")) email = getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("email", "none");

        orders = new ArrayList<Order>();
        adapter = new OrderListAdapter(this, R.layout.order_list_item, orders);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        RetrofitRequest.
        createService(STORE.API_Client.class).
        list_orders(email).
        enqueue(new Callback<STORE.ResponseModel_Order>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel_Order> call, Response<STORE.ResponseModel_Order> response) {
                loader.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if (response.body().getStatus() == 0) {
                        nothingFound.setVisibility(View.VISIBLE);
                    } else if (response.body().getStatus() == 1) {
                        List<Order> productList = response.body().getOrders();

                        for (int i = 0; i < productList.size(); i++) {
                            Order p = new Order();
                            p.id = productList.get(i).getId();
                            p.status = productList.get(i).getStatus();
                            p.email = productList.get(i).getEmail();
                            p.cc = productList.get(i).getCc();
                            p.exp = productList.get(i).getExp();
                            p.cvv = productList.get(i).getCvv();
                            p.products = productList.get(i).getProducts();
                            orders.add(p);
                        }

                        adapter.notifyDataSetChanged();
                    }

                } else {
                    Log.i("#####_failure", "" + response.code());
                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<STORE.ResponseModel_Order> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Snackbar.make(findViewById(android.R.id.content), "Cannot Connect to Server", Snackbar.LENGTH_LONG).show();
                Log.i("#####_HARD_FAIL", "StackTrace:");
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        if(mode.equals("admin")) {
            if(orders.get(i).status.equals("completed")) return;

            new AlertDialog
            .Builder(this)
            .setTitle("Complete Order")
            .setMessage("Are you sure you want to mark this order as complete?")
            .setCancelable(false)
            .setNegativeButton("NO", null)
            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    final ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(OrderActivity.this, R.style.Theme_AppCompat_Light_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Completing Order");
                    progressDialog.show();

                    RetrofitRequest.
                    createService(STORE.API_Client.class).
                    updateOrder(orders.get(i).id).
                    enqueue(new Callback<STORE.ResponseModel>() {
                        @Override
                        public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                            progressDialog.dismiss();

                            if (response.isSuccessful()) {
                                Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                                if(response.body().getStatus() == 0) {
                                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                                } else if(response.body().getStatus() == -1) {
                                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                                } else if(response.body().getStatus() == 1) {
                                    new SendMail(orders.get(i).email, "Order Started", "Hello. Your order (#" + orders.get(i).id + ") has been started. It will be delivered soon.");
                                    Toast.makeText(OrderActivity.this, "Order Completed", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(getIntent());
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
        } else if(mode.equals("user")) {
            new AlertDialog
            .Builder(this)
            .setTitle("Delete Order")
            .setMessage("Are you sure you want to delete this order?")
            .setCancelable(false)
            .setNegativeButton("NO", null)
            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    final ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(OrderActivity.this, R.style.Theme_AppCompat_Light_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Deleting Order");
                    progressDialog.show();

                    RetrofitRequest.
                    createService(STORE.API_Client.class).
                    deleteOrder(orders.get(i).id).
                    enqueue(new Callback<STORE.ResponseModel>() {
                        @Override
                        public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                            progressDialog.dismiss();

                            if (response.isSuccessful()) {
                                Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                                if(response.body().getStatus() == 0) {
                                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                                } else if(response.body().getStatus() == -1) {
                                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                                } else if(response.body().getStatus() == 1) {
                                    Toast.makeText(OrderActivity.this, "Order Deleted", Toast.LENGTH_SHORT).show();
                                    orders.remove(i);
                                    adapter.notifyDataSetChanged();
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