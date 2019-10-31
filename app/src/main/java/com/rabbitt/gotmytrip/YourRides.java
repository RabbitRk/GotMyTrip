package com.rabbitt.gotmytrip;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rabbitt.gotmytrip.DBhelper.dbHelper;
import com.rabbitt.gotmytrip.DBhelper.recycleAdapter;
import com.rabbitt.gotmytrip.PrefsManager.PrefsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rabbitt.gotmytrip.PrefsManager.PrefsManager.ID_KEY;
import static com.rabbitt.gotmytrip.PrefsManager.PrefsManager.USER_PREFS;

public class YourRides extends AppCompatActivity {

    private static final String TAG = "MaluRk";
    dbHelper database;
    RecyclerView recyclerView;
    yourRidesAdapter recycler;
    List<recycleAdapter> productAdapter;
    ProgressDialog loading;
    String userid, travel;

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

        SharedPreferences userpref;
        userpref = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        userid = userpref.getString(ID_KEY, "");

        recyclerView = findViewById(R.id.your_rides);

        loading = ProgressDialog.show(this, "Getting your rides", "Please wait", false, false);
        productAdapter = new ArrayList<>();
        //code begins
        database = new dbHelper(this);

//        database.insertdata("1", "11.09.2108", "Rental", "Auto", "Cuddalore", "45km");
//        database.insertdata("1", "11.09.2108", "Rental", "Auto", "Cuddalore", "45km");
//        database.insertdata("1", "11.09.2108", "Rental", "Auto", "Cuddalore", "45km");
//        database.insertdata("1", "11.09.2108", "Rental", "Auto", "Cuddalore", "45km");
//        AsyncTaskRunner runner = new AsyncTaskRunner();
//        runner.execute();

        productAdapter = database.getdata();

        recycler = new yourRidesAdapter(productAdapter);

        Log.i("HIteshdata", "" + productAdapter);

        RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(reLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recycler);

        PrefsManager prefsManager = new PrefsManager(this);
        travel = prefsManager.getTravel_type();



        init();
    }

    private void init() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_RIDE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //if the server response is success
                        Log.i(TAG, "Response........................" + response);
                        loading.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();

                        Log.i(TAG, "Error checking........................" + error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters otp and username
                params.put("userId", userid);
                params.put("travel_type", travel);
                return params;
            }
        };
        Log.i(TAG, "otp checking........................" + userid);
        //Adding the request to the queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

//    @SuppressLint("StaticFieldLeak")
//    class AsyncTaskRunner extends AsyncTask<String, String, List<recycleAdapter>> {
//
//        private ProgressDialog progressDialog;
//
//        @Override
//        protected List<recycleAdapter> doInBackground(String... params) {
//            productAdapter = database.getdata();
//            return productAdapter;
//        }
//
//
//        @Override
//        protected void onPostExecute(List<recycleAdapter> result) {
//            // execution of result of Long time consuming operation
//            progressDialog.dismiss();
//            recycler = new yourRidesAdapter(productAdapter);
//
//            Log.i("HIteshdata", "" + productAdapter);
//
//            RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
//            recyclerView.setLayoutManager(reLayoutManager);
//            recyclerView.setItemAnimator(new DefaultItemAnimator());
//            recyclerView.setAdapter(recycler);
//        }
//        @Override
//        protected void onPreExecute() {
//            progressDialog = ProgressDialog.show(YourRides.this,
//                    "Please wait",
//                    "Getting your ride details");
//        }
//        @Override
//        protected void onProgressUpdate(String... text) {
//        }
//    }

    public void viewData()
    {

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
