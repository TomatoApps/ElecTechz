package com.example.electechz;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.givePermission) Button givePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        givePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
            }
        });
        checkPermissions();
    }

    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        else
            checkLogin();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    checkLogin();
                else {
                    givePermission.setVisibility(View.VISIBLE);
                    Snackbar.make(findViewById(android.R.id.content), "Permission Required", Snackbar.LENGTH_INDEFINITE).show();
                }
        }
    }

    public void checkLogin() {
        if(this.getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("exists", "").equals("YES")) {
            int type = this.getSharedPreferences(STORE.SP, MODE_PRIVATE).getInt("type", 100);

            if(type == -1)
                startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
            else
                startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));

            finish();
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        }
    }
}
