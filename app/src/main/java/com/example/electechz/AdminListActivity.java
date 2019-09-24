package com.example.electechz;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.electechz.STORE.Product;
import com.example.electechz.STORE.Vacancy;
import com.example.electechz.STORE.Program;

import java.util.ArrayList;
import java.util.List;

public class AdminListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String mode;
    boolean addMenu;
    private ArrayList<Product> products;
    private ArrayList<Vacancy> vacanies;
    private ArrayList<Program> programs;
    private ProductListAdapter p_adapter;
    private VacancyListAdapter v_adapter;
    private ProgramListAdapter pro_adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.loader) ProgressBar loader;
    @BindView(R.id.nothingFound) TextView nothingFound;
    @BindView(R.id.toolbar_title) TextView toolbar_title;
    @BindView(R.id.listView) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);
        ButterKnife.bind(this);

        mode = this.getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("listMode", "Admin Panel");

        if (mode.equals("Products") || mode.equals("Vacancies") || mode.equals("Training Programs"))
            addMenu = true;

        toolbar.setTitle("");
        toolbar_title.setText(mode);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (mode.equals("Products")) {
            products = new ArrayList<Product>();
            p_adapter = new ProductListAdapter(this, R.layout.product_list_item, products);
            listView.setAdapter(p_adapter);
            listView.setOnItemClickListener(this);

            RetrofitRequest.
                    createService(STORE.API_Client.class).
                    list_products(null).
                    enqueue(new Callback<STORE.ResponseModel_Product>() {
                        @Override
                        public void onResponse(Call<STORE.ResponseModel_Product> call, Response<STORE.ResponseModel_Product> response) {
                            loader.setVisibility(View.GONE);

                            if (response.isSuccessful()) {
                                Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                                if (response.body().getStatus() == 0) {
                                    nothingFound.setVisibility(View.VISIBLE);
                                } else if (response.body().getStatus() == 1) {
                                    List<Product> productList = response.body().getProducts();

                                    for (int i = 0; i < productList.size(); i++) {
                                        Log.i("success/response", productList.get(i).getId() + " // " + productList.get(i).getName() + " // " + productList.get(i).getPrice());

                                        Product p = new Product();
                                        p.id = productList.get(i).getId();
                                        p.name = productList.get(i).getName();
                                        p.description = productList.get(i).getDescription();
                                        p.category = productList.get(i).getCategory();
                                        p.price = productList.get(i).getPrice();
                                        p.quantity = productList.get(i).getQuantity();
                                        p.filename = productList.get(i).getFilename();
                                        products.add(p);
                                    }

                                    p_adapter.notifyDataSetChanged();
                                }

                            } else {
                                Log.i("#####_failure", "" + response.code());
                                Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<STORE.ResponseModel_Product> call, Throwable t) {
                            loader.setVisibility(View.GONE);
                            Snackbar.make(findViewById(android.R.id.content), "Cannot Connect to Server", Snackbar.LENGTH_LONG).show();
                            Log.i("#####_HARD_FAIL", "StackTrace:");
                            t.printStackTrace();
                        }
                    });
        } else if (mode.equals("Vacancies")) {
            vacanies = new ArrayList<Vacancy>();
            v_adapter = new VacancyListAdapter(this, R.layout.vacancy_list_item, vacanies);
            listView.setAdapter(v_adapter);
            listView.setOnItemClickListener(this);

            RetrofitRequest.
                    createService(STORE.API_Client.class).
                    list_vacancies(null).
                    enqueue(new Callback<STORE.ResponseModel_Vacancy>() {
                        @Override
                        public void onResponse(Call<STORE.ResponseModel_Vacancy> call, Response<STORE.ResponseModel_Vacancy> response) {
                            loader.setVisibility(View.GONE);

                            if (response.isSuccessful()) {
                                Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                                if (response.body().getStatus() == 0) {
                                    nothingFound.setVisibility(View.VISIBLE);
                                } else if (response.body().getStatus() == 1) {
                                    List<Vacancy> vacancyList = response.body().getVacancies();

                                    for (int i = 0; i < vacancyList.size(); i++) {
                                        Vacancy p = new Vacancy();
                                        p.id = vacancyList.get(i).getId();
                                        p.position = vacancyList.get(i).getPosition();
                                        p.location = vacancyList.get(i).getLocation();
                                        p.salary = vacancyList.get(i).getSalary();
                                        p.details = vacancyList.get(i).getDetails();
                                        p.category = vacancyList.get(i).getCategory();
                                        vacanies.add(p);
                                    }

                                    v_adapter.notifyDataSetChanged();
                                }

                            } else {
                                Log.i("#####_failure", "" + response.code());
                                Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<STORE.ResponseModel_Vacancy> call, Throwable t) {
                            loader.setVisibility(View.GONE);
                            Snackbar.make(findViewById(android.R.id.content), "Cannot Connect to Server", Snackbar.LENGTH_LONG).show();
                            Log.i("#####_HARD_FAIL", "StackTrace:");
                            t.printStackTrace();
                        }
                    });
        } else if (mode.equals("Training Programs")) {
            programs = new ArrayList<Program>();
            pro_adapter = new ProgramListAdapter(this, R.layout.program_list_item, programs);
            listView.setAdapter(pro_adapter);
            listView.setOnItemClickListener(this);

            RetrofitRequest.
                    createService(STORE.API_Client.class).
                    list_programs(null).
                    enqueue(new Callback<STORE.ResponseModel_Program>() {
                        @Override
                        public void onResponse(Call<STORE.ResponseModel_Program> call, Response<STORE.ResponseModel_Program> response) {
                            loader.setVisibility(View.GONE);

                            if (response.isSuccessful()) {
                                Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                                if (response.body().getStatus() == 0) {
                                    nothingFound.setVisibility(View.VISIBLE);
                                } else if (response.body().getStatus() == 1) {
                                    List<Program> programList = response.body().getPrograms();

                                    for (int i = 0; i < programList.size(); i++) {
                                        Program p = new Program();
                                        p.id = programList.get(i).getId();
                                        p.title = programList.get(i).getTitle();
                                        p.location = programList.get(i).getLocation();
                                        p.date = programList.get(i).getDate();
                                        p.details = programList.get(i).getDetails();
                                        p.category = programList.get(i).getCategory();
                                        programs.add(p);
                                    }

                                    pro_adapter.notifyDataSetChanged();
                                }

                            } else {
                                Log.i("#####_failure", "" + response.code());
                                Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<STORE.ResponseModel_Program> call, Throwable t) {
                            loader.setVisibility(View.GONE);
                            Snackbar.make(findViewById(android.R.id.content), "Cannot Connect to Server", Snackbar.LENGTH_LONG).show();
                            Log.i("#####_HARD_FAIL", "StackTrace:");
                            t.printStackTrace();
                        }
                    });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (mode.equals("Products")) {
            Intent i = new Intent(this, AdminProductEditActivity.class);
            i.putExtra("id", products.get(position).id);
            i.putExtra("name", products.get(position).name);
            i.putExtra("description", products.get(position).description);
            i.putExtra("category", products.get(position).category);
            i.putExtra("price", products.get(position).price + "");
            i.putExtra("quantity", products.get(position).quantity + "");
            i.putExtra("filename", products.get(position).filename);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        } else if (mode.equals("Vacancies")) {
            Intent i = new Intent(this, AdminVacancyEditActivity.class);
            i.putExtra("id", vacanies.get(position).id);
            i.putExtra("position", vacanies.get(position).position);
            i.putExtra("location", vacanies.get(position).location);
            i.putExtra("salary", vacanies.get(position).salary);
            i.putExtra("details", vacanies.get(position).details);
            i.putExtra("category", vacanies.get(position).category);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        } else if (mode.equals("Training Programs")) {
            Intent i = new Intent(this, AdminProgramEditActivity.class);
            i.putExtra("id", programs.get(position).id);
            i.putExtra("title", programs.get(position).title);
            i.putExtra("location", programs.get(position).location);
            i.putExtra("date", programs.get(position).date);
            i.putExtra("details", programs.get(position).details);
            i.putExtra("category", programs.get(position).category);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (addMenu) getMenuInflater().inflate(R.menu.admin_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            if (mode.equals("Products")) {
                startActivity(new Intent(getApplicationContext(), AdminProductAddActivity.class));
                finish();
                overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
            } else if (mode.equals("Vacancies")) {
                startActivity(new Intent(getApplicationContext(), AdminVacancyAddActivity.class));
                finish();
                overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
            } else if (mode.equals("Training Programs")) {
                startActivity(new Intent(getApplicationContext(), AdminProgramAddActivity.class));
                finish();
                overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
            }
        }
        return super.onOptionsItemSelected(item);
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