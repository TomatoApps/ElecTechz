package com.example.electechz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdminHomeActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.btn1) CardView btn1;
    @BindView(R.id.btn2) CardView btn2;
    @BindView(R.id.btn3) CardView btn3;
    @BindView(R.id.btn4) CardView btn4;
    @BindView(R.id.btn5) CardView btn5;
    @BindView(R.id.btn6) CardView btn6;
    @BindView(R.id.btn7) CardView btn7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btn1.getId()) {
            getSharedPreferences(STORE.SP, MODE_PRIVATE).edit().putString("listMode", "Products").apply();
            startActivity(new Intent(getApplicationContext(), AdminListActivity.class));
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        } else if(view.getId() == btn2.getId()) {
            getSharedPreferences(STORE.SP, MODE_PRIVATE).edit().putString("listMode", "Vacancies").apply();
            startActivity(new Intent(getApplicationContext(), AdminListActivity.class));
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        } else if(view.getId() == btn3.getId()) {
            getSharedPreferences(STORE.SP, MODE_PRIVATE).edit().putString("listMode", "Training Programs").apply();
            startActivity(new Intent(getApplicationContext(), AdminListActivity.class));
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        } else if(view.getId() == btn4.getId()) {
            Intent i = new Intent(this, OrderActivity.class);
            i.putExtra("mode", "admin");
            startActivity(i);
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        } else if(view.getId() == btn5.getId()) {
            Intent i = new Intent(this, AppActivity.class);
            i.putExtra("mode", "admin");
            i.putExtra("type", "1");
            startActivity(i);
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        } else if(view.getId() == btn6.getId()) {
            Intent i = new Intent(this, AppActivity.class);
            i.putExtra("mode", "admin");
            i.putExtra("type", "2");
            startActivity(i);
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        } else if(view.getId() == btn7.getId()) {
            startActivity(new Intent(getApplicationContext(), UserListActivity.class));
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_signout) {
            SharedPreferences.Editor editor = getSharedPreferences(STORE.SP, MODE_PRIVATE).edit();
            editor.putString("exists", "NO");
            editor.apply();

            startActivity(new Intent(this, LoginActivity.class));
            finish();
            overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
            return true;
        } else if(item.getItemId() == R.id.action_edit_profile) {
            Intent i3 = new Intent(this, EditProfileActivity.class);
            i3.putExtra("mode", "admin");
            startActivity(i3);
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}