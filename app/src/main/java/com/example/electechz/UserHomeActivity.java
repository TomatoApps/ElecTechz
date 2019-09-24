package com.example.electechz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.electechz.STORE.Product;
import com.example.electechz.STORE.Vacancy;
import com.example.electechz.STORE.Program;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    int type;
    final ArrayList<Product> products = new ArrayList<Product>();
    final ArrayList<Vacancy> vacancies = new ArrayList<Vacancy>();
    final ArrayList<Program> programs = new ArrayList<Program>();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.loader) ProgressBar loader;
    @BindView(R.id.nothingFound) TextView nothingFound;
    @BindView(R.id.listView) ListView listView;
    @BindView(R.id.categorySpinnerUser) Spinner categorySpinnerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        type = this.getSharedPreferences(STORE.SP, MODE_PRIVATE).getInt("type", -1);
        int spinnerArray = 0;

        if(type == 0) spinnerArray = R.array.products;
        else if(type == 1) spinnerArray = R.array.vacancies;
        else if(type == 2) spinnerArray = R.array.programs;

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, spinnerArray, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinnerUser.setAdapter(adapter);
        categorySpinnerUser.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        listView.setVisibility(View.GONE);
        nothingFound.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);

        products.clear();
        vacancies.clear();
        programs.clear();

        if(type == 0) showProducts();
        else if(type == 1) showVacancies();
        else if(type == 2) showPrograms();
    }

    public void showProducts() {
        final ProductListAdapter adapter = new ProductListAdapter(this, R.layout.product_list_item, products);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        RetrofitRequest.
        createService(STORE.API_Client.class).
        list_products(categorySpinnerUser.getSelectedItem().toString()).
        enqueue(new Callback<STORE.ResponseModel_Product>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel_Product> call, Response<STORE.ResponseModel_Product> response) {
                loader.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if (response.body().getStatus() == 0) {
                        nothingFound.setVisibility(View.VISIBLE);
                    } else if (response.body().getStatus() == 1) {
                        listView.setVisibility(View.VISIBLE);
                        List<Product> productList = response.body().getProducts();

                        for (int i = 0; i < productList.size(); i++) {
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

                        adapter.notifyDataSetChanged();
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
    }

    public void showVacancies() {
        final VacancyListAdapter adapter = new VacancyListAdapter(this, R.layout.vacancy_list_item, vacancies);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        RetrofitRequest.
        createService(STORE.API_Client.class).
        list_vacancies(categorySpinnerUser.getSelectedItem().toString()).
        enqueue(new Callback<STORE.ResponseModel_Vacancy>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel_Vacancy> call, Response<STORE.ResponseModel_Vacancy> response) {
                loader.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if (response.body().getStatus() == 0) {
                        nothingFound.setVisibility(View.VISIBLE);
                    } else if (response.body().getStatus() == 1) {
                        listView.setVisibility(View.VISIBLE);
                        List<Vacancy> vacancyList = response.body().getVacancies();

                        for (int i = 0; i < vacancyList.size(); i++) {
                            Vacancy p = new Vacancy();
                            p.id = vacancyList.get(i).getId();
                            p.position = vacancyList.get(i).getPosition();
                            p.location = vacancyList.get(i).getLocation();
                            p.salary = vacancyList.get(i).getSalary();
                            p.details = vacancyList.get(i).getDetails();
                            p.category = vacancyList.get(i).getCategory();
                            vacancies.add(p);
                        }

                        adapter.notifyDataSetChanged();
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
    }

    public void showPrograms() {
        final ProgramListAdapter adapter = new ProgramListAdapter(this, R.layout.program_list_item, programs);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        RetrofitRequest.
        createService(STORE.API_Client.class).
        list_programs(categorySpinnerUser.getSelectedItem().toString()).
        enqueue(new Callback<STORE.ResponseModel_Program>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel_Program> call, Response<STORE.ResponseModel_Program> response) {
                loader.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if (response.body().getStatus() == 0) {
                        nothingFound.setVisibility(View.VISIBLE);
                    } else if (response.body().getStatus() == 1) {
                        listView.setVisibility(View.VISIBLE);
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

                        adapter.notifyDataSetChanged();
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (type == 0) {
            Intent i = new Intent(this, ProductDetailsActivity.class);
            i.putExtra("id", products.get(position).id);
            i.putExtra("name", products.get(position).name);
            i.putExtra("description", products.get(position).description);
            i.putExtra("category", products.get(position).category);
            i.putExtra("price", products.get(position).price + "");
            i.putExtra("quantity", products.get(position).quantity + "");
            i.putExtra("filename", products.get(position).filename);
            startActivity(i);
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        } else if (type == 1) {
            Intent i = new Intent(this, VacancyAndProgramDetailsActivity.class);
            i.putExtra("mode", "Vacancy");
            i.putExtra("id", vacancies.get(position).id);
            i.putExtra("title", vacancies.get(position).position);
            i.putExtra("location", vacancies.get(position).location);
            i.putExtra("salaryDate", vacancies.get(position).salary);
            i.putExtra("details", vacancies.get(position).details);
            i.putExtra("category", vacancies.get(position).category);
            startActivity(i);
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        } else if (type == 2) {
            Intent i = new Intent(this, VacancyAndProgramDetailsActivity.class);
            i.putExtra("mode", "Program");
            i.putExtra("id", programs.get(position).id);
            i.putExtra("title", programs.get(position).title);
            i.putExtra("location", programs.get(position).location);
            i.putExtra("salaryDate", programs.get(position).date);
            i.putExtra("details", programs.get(position).details);
            i.putExtra("category", programs.get(position).category);
            startActivity(i);
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (type) {
            case 0:
                getMenuInflater().inflate(R.menu.customer_home_menu, menu);
                break;
            case 1:
                getMenuInflater().inflate(R.menu.employee_home_menu, menu);
                break;
            case 2:
                getMenuInflater().inflate(R.menu.trainee_home_menu, menu);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signout:
                SharedPreferences.Editor editor = getSharedPreferences(STORE.SP, MODE_PRIVATE).edit();
                editor.putString("exists", "NO");
                editor.apply();

                startActivity(new Intent(this, LoginActivity.class));
                finish();
                overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
                break;
            case R.id.action_cart:
                startActivity(new Intent(this, CartActivity.class));
                overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
                break;
            case R.id.action_order:
                Intent i = new Intent(this, OrderActivity.class);
                i.putExtra("mode", "user");
                startActivity(i);
                overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
                break;
            case R.id.action_apps:
                Intent i2 = new Intent(this, AppActivity.class);
                i2.putExtra("mode", "user");
                i2.putExtra("type", "" + type);
                startActivity(i2);
                overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
                break;
            case R.id.action_edit_profile:
                Intent i3 = new Intent(this, EditProfileActivity.class);
                i3.putExtra("mode", "user");
                startActivity(i3);
                overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
                break;
            case R.id.action_info:
                startActivity(new Intent(this, InfoActivity.class));
                overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
                break;
            case R.id.action_feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
}
