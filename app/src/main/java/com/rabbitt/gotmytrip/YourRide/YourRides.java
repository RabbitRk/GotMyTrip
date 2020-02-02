package com.rabbitt.gotmytrip.YourRide;

import android.annotation.SuppressLint;
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
import com.rabbitt.gotmytrip.Config;
import com.rabbitt.gotmytrip.PrefsManager.PrefsManager;
import com.rabbitt.gotmytrip.R;
import com.rabbitt.gotmytrip.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rabbitt.gotmytrip.PrefsManager.PrefsManager.ID_KEY;
import static com.rabbitt.gotmytrip.PrefsManager.PrefsManager.USER_PREFS;

//import com.rabbitt.gotmytrip.DBhelper.dbHelper;

public class YourRides extends AppCompatActivity {

    private static final String TAG = "MaluRk";
//    dbHelper database;
    RecyclerView recyclerView;
    yourRidesAdapter recycler;
    ModalAdapter model;
    List<ModalAdapter> productAdapter;
    List<ModalAdapter> data = new ArrayList<>();
    ProgressDialog loading;
    String userid;
    PrefsManager prefsManager;
    yourRidesAdapter recycleadapter;

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

        recycler = new yourRidesAdapter(productAdapter);

        Log.i("HIteshdata", "" + productAdapter);

//        RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setLayoutManager(reLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(recycler);

        prefsManager = new PrefsManager(this);

            init();


    }

    private void init() {

        loading = ProgressDialog.show(this, "Getting your rides", "Please wait", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_RIDE,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        //if the server response is success
                        Log.i(TAG, "Response........................" + response);

                        Log.i(TAG, "Responce.............." + response);
                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject jb;// = arr.getJSONObject(0);
                            int n = arr.length();
/*
*  [book_id] => 3
            [prefix] => CTY
            [starting_point] => adsf, Tamil Nadu 607001, India,nullasdf
            [destination_point] => NH32, Tamil Nadu 607402, India,null
            [dateon] => 2020-02-02
            [status] => 0
            [v_type] => Prime
*
* */
                            for (int i = 0; i < n; i++) {
                                jb = arr.getJSONObject(i);
                                model = new ModalAdapter();
                                model.setV_type(jb.getString("v_type"));
                                model.setBook_id(jb.getString("book_id"));
                                model.setStart(jb.getString("starting_point"));
                                model.setDate(jb.getString("dateon"));
                                model.setPrefix(jb.getString("prefix"));
                                model.setEnd(jb.getString("destination_point"));

                                data.add(model);
                            }

                            updaterecyclershit(data);

                        } catch (JSONException e) {
                            Log.i(TAG, "Error: " + e.getMessage());
                        }





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
//                params.put("travel_type", travel);
                return params;
            }
        };
        Log.i(TAG, "otp checking........................" + userid);
        //Adding the request to the queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }



    private void updaterecyclershit(List<ModalAdapter> datam) {


        Log.i(TAG, "Current thread:update " + Thread.currentThread().getId());
        if (datam != null) {

            recycleadapter = new yourRidesAdapter(datam);
            Log.i("HIteshdata", "" + datam);

            LinearLayoutManager reLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(reLayoutManager);

            reLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(recycleadapter);

            recycleadapter.notifyDataSetChanged();
        }
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

    public void viewData() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refresh_your_rides, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionsearch) {
            init();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


//    public void cancel_btn(View view) {
//
//        builder = new AlertDialog.Builder(this);
//
//        //Setting message manually and performing action on button click
//        builder.setMessage("Do you really want to cancel your ride ?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                        cancel_ride();
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        //  Action for 'NO' Button
//                        dialog.cancel();
//                    }
//                });
//        //Creating dialog box
//        AlertDialog alert = builder.create();
//        //Setting the title manually
//        alert.setTitle("Confirmation");
//        alert.show();
//    }

//    private void cancel_ride() {
//        loading = ProgressDialog.show(this, "Cancelling your rides", "Please wait", false, false);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CANCEL_RIDE, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.i(TAG, "Responce..." + response);
//                loading.dismiss();
//                if (response.equals("succeses")) {
//                    prefsManager.setTravel_type("");
//                    Toast.makeText(YourRides.this, "Cancelled successfully", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                loading.dismiss();
//                Log.i("Error", "volley response error");
//                Toast.makeText(getApplicationContext(), "Responce error failed   " + error.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("cus_id", userid);
//                params.put("book_id", book_id_);
//                params.put("prefix", prefix_);
//                return params;
//            }
//        };
//
//        //inseting into  the iteluser table
//        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
//    }

//    public void status_btn(View view) {
//
//        LayoutInflater inflater = getLayoutInflater();
//        View alertLayout = inflater.inflate(R.layout.ride_status, null);
//        final TextView dri_name = alertLayout.findViewById(R.id.dri_name);
//        final TextView dri_phone = alertLayout.findViewById(R.id.dri_phone);
//        final TextView v_number = alertLayout.findViewById(R.id.v_number);
//        alertLayout.setBackgroundColor(ContextCompat.getColor(this ,R.color.bgcolor));
//
//        loading = ProgressDialog.show(this, "Fetching your ride status", "Please wait", false, false);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_DRIVER, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                loading.dismiss();
//                Log.i("Responce......aoutside", response);
//                if (!response.equals("")) {
//                    Log.i("Responce....ain", response);
//                    try {
//                        JSONArray arr = new JSONArray(response);
//                        JSONObject jb = arr.getJSONObject(0);
//
//                        dri_name_ = jb.getString("dri_first_name");
//                        dri_phone_ = jb.getString("dri_phone");
//                        dri_v_number_ = jb.getString("v_no");
//
//                        dri_name.setText(dri_name_);
//                        dri_phone.setText(dri_phone_);
//                        v_number.setText(dri_v_number_);
//
//                    } catch (JSONException e) {
//                        Log.i("Error on catch.....", e.getMessage());
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.i("Responce.............", response);
//                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                loading.dismiss();
//                Log.i("Error", "volley response error");
//                Toast.makeText(getApplicationContext(), "Responce error failed   " + error.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("BOOK_ID", book_id_);
//                params.put("CUS_ID", userid);
//                return params;
//            }
//        };
//
//        //inseting into  the iteluser table
//        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
//
//
//        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setTitle("Info");
//        // this is set the view from XML inside AlertDialog
//        alert.setView(alertLayout);
//        // disallow cancel of AlertDialog on click of back button and outside touch
//        alert.setCancelable(false);
//        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////                citybooking();
//            }
//        });
//        AlertDialog dialog = alert.create();
//        dialog.show();
//    }
//
//    public void call_driver(View view) {
//
//        try {
//            if (!dri_phone_.equals("")) {
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:" + dri_phone_));
//                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Please grant permission to make phone call.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                startActivity(callIntent);
//            }
//        } catch (Exception ex) {
//            Log.i(TAG, "makeCall: " + ex.getMessage());
//        }
//    }
}
