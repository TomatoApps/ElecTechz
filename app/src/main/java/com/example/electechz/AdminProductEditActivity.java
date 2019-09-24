package com.example.electechz;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductEditActivity extends AppCompatActivity implements View.OnClickListener {

    Uri uri;
    String imageFileName = "";
    String id = "", filename = "";
    boolean imageFlag = false;
    ProgressDialog progressDialog;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.productName) EditText productName;
    @BindView(R.id.productDescription) EditText productDescription;
    @BindView(R.id.productPrice) EditText productPrice;
    @BindView(R.id.productQuantity) EditText productQuantity;
    @BindView(R.id.categorySpinner) Spinner categorySpinner;
    @BindView(R.id.uploadPicture) Button uploadPicture;
    @BindView(R.id.uploadProduct) Button uploadProduct;
    @BindView(R.id.productImage) ImageView productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_edit);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        uploadPicture.setOnClickListener(this);
        uploadProduct.setOnClickListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.products, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        id = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        String category = getIntent().getStringExtra("category");
        String price = getIntent().getStringExtra("price");
        String quantity = getIntent().getStringExtra("quantity");
        filename = getIntent().getStringExtra("filename");

        productName.setText(name);
        productDescription.setText(description);
        productPrice.setText(price);
        productQuantity.setText(quantity);
        setSpinner(category);
        if(!filename.equals("default")) Glide.with(this).load(STORE.BASE_URL + "images/" + filename + ".jpg").thumbnail(0.1f).into(productImage);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == uploadPicture.getId()) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
            else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        } else if(view.getId() == uploadProduct.getId()) {
            validateInput();
        }
    }

    public void validateInput() {
        boolean valid = true;

        String _productName = productName.getText().toString();
        String _productDescription = productDescription.getText().toString();
        String _productPrice = productPrice.getText().toString();
        String _productQuantity = productQuantity.getText().toString();

        if (_productName.isEmpty()) {
            productName.setError("Name can't be empty");
            valid = false;
        } else {
            productName.setError(null);
        }

        if (_productDescription.isEmpty()) {
            productDescription.setError("Description can't be empty");
            valid = false;
        } else {
            productDescription.setError(null);
        }

        if (_productPrice.isEmpty()) {
            productPrice.setError("Price can't be empty");
            valid = false;
        } else {
            productPrice.setError(null);
        }

        if (_productQuantity.isEmpty()) {
            productQuantity.setError("Quantity can't be empty");
            valid = false;
        } else {
            productQuantity.setError(null);
        }

        if(valid) update();
    }

    public void update() {
        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating Product");
        progressDialog.show();

        String _productName = productName.getText().toString();
        String _productDescription = productDescription.getText().toString();
        String _productCategory = categorySpinner.getSelectedItem().toString();
        String _productPrice = productPrice.getText().toString();
        String _productQuantity = productQuantity.getText().toString();
        if(imageFlag) imageFileName = String.valueOf(Calendar.getInstance().getTimeInMillis());
        else imageFileName = filename;

        RetrofitRequest.
        createService(STORE.API_Client.class).
        updateProduct(id, _productName, _productDescription, _productCategory, _productPrice, _productQuantity, imageFileName).
        enqueue(new Callback<STORE.ResponseModel>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if(response.body().getStatus() == -1) {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == 0) {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == 1) {
                        if(imageFlag)
                            uploadImage();
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(AdminProductEditActivity.this, "Product Updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), AdminListActivity.class));
                            finish();
                            overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
                        }
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

    public void uploadImage() {
        Log.i("#####_UPLOADING", "IMAGE");

        File file = new File(URIPathLib.getPath(this, uri));
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), imageFileName);

        RetrofitRequest.
        createService(STORE.API_Client.class).
        uploadImage(image, filename).
        enqueue(new Callback<STORE.ResponseModel>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if(response.body().getStatus() == 0) {
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == 1) {
                        Toast.makeText(AdminProductEditActivity.this, "Product Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AdminListActivity.class));
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

    public void setSpinner(String category) {
        switch (category) {
            case "Automotive":
                categorySpinner.setSelection(0);
                break;
            case "Electrical":
                categorySpinner.setSelection(1);
                break;
            case "Construction":
                categorySpinner.setSelection(2);
                break;
            case "Plumbing":
                categorySpinner.setSelection(3);
                break;
            case "Soldering":
                categorySpinner.setSelection(4);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            new AlertDialog
                .Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setCancelable(false)
                .setNegativeButton("NO", null)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        progressDialog = new ProgressDialog(AdminProductEditActivity.this, R.style.Theme_AppCompat_Light_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Deleting Product");
                        progressDialog.show();

                        RetrofitRequest.
                        createService(STORE.API_Client.class).
                        deleteProduct(id).
                        enqueue(new Callback<STORE.ResponseModel>() {
                            @Override
                            public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                                progressDialog.dismiss();

                                if (response.isSuccessful()) {
                                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                                    if(response.body().getStatus() == 0) {
                                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                                    } else if(response.body().getStatus() == 1) {
                                        Toast.makeText(AdminProductEditActivity.this, "Product Deleted", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), AdminListActivity.class));
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
                    }})
                .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            try {
                productImage.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
                uri = data.getData();
                imageFlag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), AdminListActivity.class));
        finish();
        overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}