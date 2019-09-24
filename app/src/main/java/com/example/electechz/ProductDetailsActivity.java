package com.example.electechz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.productName) TextView productName;
    @BindView(R.id.productCategory) TextView productCategory;
    @BindView(R.id.productStats) TextView productStats;
    @BindView(R.id.productDescription) TextView productDescription;
    @BindView(R.id.productImage) ImageView productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        productName.setText(getIntent().getStringExtra("name"));
        productCategory.setText(getIntent().getStringExtra("category"));
        productStats.setText("$" + getIntent().getStringExtra("price") + " (" + getIntent().getStringExtra("quantity") + ") in stock");
        productDescription.setText(getIntent().getStringExtra("description"));
        Glide.with(this).load(STORE.BASE_URL + "images/" + getIntent().getStringExtra("filename") + ".jpg").thumbnail(0.1f).into(productImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_addd_to_cart) {
            SQLiteDatabase db = openOrCreateDatabase("ElecTechz", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS cart (id TEXT, email TEXT, name TEXT, price TEXT, filename TEXT);");
            db.execSQL("INSERT INTO cart VALUES('" + getIntent().getStringExtra("id") + "', '" + this.getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("email", "?") + "', '" + getIntent().getStringExtra("name").replaceAll("'", "''") + "', '" + getIntent().getStringExtra("price") + "', '" + getIntent().getStringExtra("filename") + "');");
            db.close();

            Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(R.anim.enter_right, R.anim.exit_right);

            return true;
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