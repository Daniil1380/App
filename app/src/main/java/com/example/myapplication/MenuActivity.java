package com.example.myapplication;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;

public class MenuActivity extends AppCompatActivity {
    NavController navController;
    private RecyclerView recyclerView;
    private BibLibAdapter bibLibAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        navController = Navigation.findNavController(this, R.id.fragmentStart);
        getSupportActionBar().hide();


    }

    public void onFindClick(View view) {
        navController.navigate(R.id.findFragment);
    }

    public void onNewsClick(View view) {
        navController.navigate(R.id.newsFragment);
    }

    public void onProfileClick(View view) {
        navController.navigate(R.id.profileFragment);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }


}
