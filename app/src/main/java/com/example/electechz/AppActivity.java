package com.example.electechz;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.example.electechz.STORE.App;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String email = null, type = "", mode = "";
    ArrayList<App> apps;
    AppListAdapter adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbar_title;
    @BindView(R.id.loader) ProgressBar loader;
    @BindView(R.id.nothingFound) TextView nothingFound;
    @BindView(R.id.listView) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mode = getIntent().getStringExtra("mode");
        if(mode.equals("admin") && getIntent().getStringExtra("type").equals("1")) toolbar_title.setText("CVs");
        type = (getIntent().getStringExtra("type").equals("1") ? "vacancy" : "program");
        if(mode.equals("user")) email = getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("email", "none");

        apps = new ArrayList<App>();
        adapter = new AppListAdapter(this, R.layout.app_list_item, apps);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        RetrofitRequest.
        createService(STORE.API_Client.class).
        list_apps(type, email).
        enqueue(new Callback<STORE.ResponseModel_App>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel_App> call, Response<STORE.ResponseModel_App> response) {
                loader.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if (response.body().getStatus() == 0) {
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if (response.body().getStatus() == -1) {
                        nothingFound.setVisibility(View.VISIBLE);
                    } else if (response.body().getStatus() == 1) {
                        List<App> productList = response.body().getApps();

                        for (int i = 0; i < productList.size(); i++) {
                            App p = new App();
                            p.id = productList.get(i).getId();
                            p.mode = productList.get(i).getMode();
                            p.email = productList.get(i).getEmail();
                            p.title = productList.get(i).getTitle();
                            p.location = productList.get(i).getLocation();
                            p.category = productList.get(i).getCategory();
                            p.salaryDate = productList.get(i).getSalaryDate();
                            p.status = productList.get(i).getStatus();
                            apps.add(p);
                        }

                        adapter.notifyDataSetChanged();
                    }

                } else {
                    Log.i("#####_failure", "" + response.code());
                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<STORE.ResponseModel_App> call, Throwable t) {
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
            if(apps.get(i).status.equals("accepted")) return;

            new AlertDialog
            .Builder(this)
            .setTitle("Accept Application")
            .setMessage("Are you sure you want to accept this application?")
            .setCancelable(false)
            .setNegativeButton("NO", null)
            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    final ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(AppActivity.this, R.style.Theme_AppCompat_Light_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Processing");
                    progressDialog.show();

                    RetrofitRequest.
                    createService(STORE.API_Client.class).
                    updateApp(apps.get(i).id).
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

                                    if(apps.get(i).mode.equals("vacancy"))
                                        new SendMail(apps.get(i).email, "Vacancy Application Accepted", "Hello. Your application for '" + apps.get(i).title + "' vacancy has been accepted.");
                                    else if(apps.get(i).mode.equals("program"))
                                        new SendMail(apps.get(i).email, "Training Program Application Accepted", "Hello. Your application for '" + apps.get(i).title + "' program has been accepted.");

                                    Toast.makeText(AppActivity.this, "Application Accepted", Toast.LENGTH_SHORT).show();
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
            .setTitle("Delete Application")
            .setMessage("Are you sure you want to delete this application?")
            .setCancelable(false)
            .setNegativeButton("NO", null)
            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    final ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(AppActivity.this, R.style.Theme_AppCompat_Light_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Deleting Application");
                    progressDialog.show();

                    RetrofitRequest.
                    createService(STORE.API_Client.class).
                    deleteApp(apps.get(i).id).
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
                                    Toast.makeText(AppActivity.this, "Order Deleted", Toast.LENGTH_SHORT).show();
                                    apps.remove(i);
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