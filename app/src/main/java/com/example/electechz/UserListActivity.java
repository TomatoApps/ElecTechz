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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.electechz.STORE.User;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    ArrayList<User> users;
    UserListAdapter adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.loader) ProgressBar loader;
    @BindView(R.id.nothingFound) TextView nothingFound;
    @BindView(R.id.listView) ListView listView;
    @BindView(R.id.categorySpinnerUser) Spinner categorySpinnerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.users, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinnerUser.setAdapter(adapter);
        categorySpinnerUser.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        listView.setVisibility(View.GONE);
        nothingFound.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        showUsers();
    }

    private void showUsers() {
        users = new ArrayList<User>();
        adapter = new UserListAdapter(this, R.layout.user_list_item, users);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        RetrofitRequest.
        createService(STORE.API_Client.class).
        list_users(categorySpinnerUser.getSelectedItemPosition()).
        enqueue(new Callback<STORE.ResponseModel_User>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel_User> call, Response<STORE.ResponseModel_User> response) {
                loader.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if (response.body().getStatus() == 0) {
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if (response.body().getStatus() == -1) {
                        nothingFound.setVisibility(View.VISIBLE);
                    } else if (response.body().getStatus() == 1) {
                        listView.setVisibility(View.VISIBLE);
                        List<User> productList = response.body().getUsers();

                        for (int i = 0; i < productList.size(); i++) {
                            User p = new User();
                            p.id = productList.get(i).getId();
                            p.name = productList.get(i).getName();
                            p.email = productList.get(i).getEmail();
                            p.mobile = productList.get(i).getMobile();
                            p.address = productList.get(i).getAddress();
                            users.add(p);
                        }

                        adapter.notifyDataSetChanged();
                    }

                } else {
                    Log.i("#####_failure", "" + response.code());
                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<STORE.ResponseModel_User> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Snackbar.make(findViewById(android.R.id.content), "Cannot Connect to Server", Snackbar.LENGTH_LONG).show();
                Log.i("#####_HARD_FAIL", "StackTrace:");
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        new AlertDialog
        .Builder(this)
        .setTitle("Remove User")
        .setMessage("Are you sure you want to remove this user?")
        .setCancelable(false)
        .setNegativeButton("NO", null)
        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(UserListActivity.this, R.style.Theme_AppCompat_Light_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Processing");
                progressDialog.show();

                RetrofitRequest.
                createService(STORE.API_Client.class).
                deleteUser(categorySpinnerUser.getSelectedItemPosition(), users.get(i).id).
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
                                Toast.makeText(UserListActivity.this, "User Removed", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

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