package com.example.electechz;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import com.example.electechz.STORE.Product;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nothingFound) TextView nothingFound;
    @BindView(R.id.listView) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final ArrayList<Product> products = new ArrayList<Product>();
        final ProductListAdapter adapter = new ProductListAdapter(this, R.layout.product_list_item, products);
        listView.setAdapter(adapter);

        try {
            SQLiteDatabase db = openOrCreateDatabase("ElecTechz", MODE_PRIVATE, null);
            Cursor c = db.rawQuery("SELECT * from cart WHERE email ='" + getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("email", "none") + "'", null);
            c.moveToFirst();

            if(c.getCount() > 0) {
                for (int i = 0; i < c.getCount(); i++, c.moveToNext())
                {
                    Product p = new Product();
                    p.cartmode = true;
                    p.id = c.getString(c.getColumnIndex("id"));
                    p.name = c.getString(c.getColumnIndex("name"));
                    p.price = c.getString(c.getColumnIndex("price"));
                    p.filename = c.getString(c.getColumnIndex("filename"));
                    products.add(p);
                }
            } else
                nothingFound.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(CartActivity.this);
                builder.setTitle("Remove Product?")
                        .setMessage("Are you sure you want to remove this product from cart?")
                        .setCancelable(false)
                        .setNegativeButton("NO", null)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDatabase db = openOrCreateDatabase("ElecTechz", MODE_PRIVATE, null);
                                db.delete("cart","id=?",new String[]{String.valueOf(products.get(position).id)});
                                db.close();
                                products.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(nothingFound.getVisibility() == View.GONE)
            getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_checkout) {
            startActivity(new Intent(this, PaymentActivity.class));
            finish();
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
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