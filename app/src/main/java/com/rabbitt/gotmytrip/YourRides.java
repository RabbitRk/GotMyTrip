package com.rabbitt.gotmytrip;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rabbitt.gotmytrip.DBhelper.dbHelper;
import com.rabbitt.gotmytrip.DBhelper.recycleAdapter;

import java.util.ArrayList;
import java.util.List;

public class YourRides extends AppCompatActivity {

    String val;
    dbHelper database;
    RecyclerView recyclerView;
    yourRidesAdapter recycler;
    List<recycleAdapter> productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_rides);

        Toolbar toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);

        //get tool bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //toolbar action to go back is any activity exists
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.your_rides);

        productAdapter = new ArrayList<>();
        //code begins
        database = new dbHelper(this);

        database.insertdata("1", "11.09.2108", "Rental", "Auto", "Cuddalore", "45km");
        database.insertdata("1", "11.09.2108", "Rental", "Auto", "Cuddalore", "45km");
        database.insertdata("1", "11.09.2108", "Rental", "Auto", "Cuddalore", "45km");
        database.insertdata("1", "11.09.2108", "Rental", "Auto", "Cuddalore", "45km");

        productAdapter = database.getdata();

        recycler = new yourRidesAdapter(productAdapter);

        Log.i("HIteshdata", "" + productAdapter);

        RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(reLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(recycler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refresh_your_rides, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionsearch:
                Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
