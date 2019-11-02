package com.rabbitt.gotmytrip;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    CardView currentRide, noRide;
    TextView vtype, book_id, start, travelTxt, dest, date, rentalTxt;
    String vtype_, book_id_, start_, travelTxt_, dest_, date_, rentalTxt_, prefix_;
    AlertDialog.Builder builder;
    PrefsManager prefsManager;

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
        currentRide = findViewById(R.id.p1card);
        noRide = findViewById(R.id.noride);

        vtype = findViewById(R.id.v_type);
        book_id = findViewById(R.id.book_id);
        start = findViewById(R.id.ori);
        travelTxt = findViewById(R.id.travel_type);
        dest = findViewById(R.id.dest);
        date = findViewById(R.id.dateof);
        rentalTxt = findViewById(R.id.rental_p);

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

        prefsManager = new PrefsManager(this);
        travel = prefsManager.getTravel_type();

        if (travel.equals(""))
        {
            noRide.setVisibility(View.VISIBLE);
        }
        else
        {
            init();
        }


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
                        if (response.equals("[]"))
                        {
                            noRide.setVisibility(View.VISIBLE);
                            if(currentRide.getVisibility() == View.VISIBLE)
                            {
                                currentRide.setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            if(noRide.getVisibility() == View.VISIBLE)
                            {
                                noRide.setVisibility(View.GONE);
                            }

                            currentRide.setVisibility(View.VISIBLE);

                            try {
                                JSONArray arr = new JSONArray(response);
                                JSONObject jb = arr.getJSONObject(0);

                                book_id_ = jb.getString("book_id");
                                prefix_ = jb.getString("prefix");
                                start_ = jb.getString("starting_point");
                                dest_ = jb.getString("destination_point");
                                vtype_ = jb.getString("v_type");
                                date_ = jb.getString("dateon");

                                Log.i("fare.......", vtype_);
                                Log.i("per km.......", dest_);

                                switch (prefix_)
                                {
                                    case "RNT":
                                        rentalTxt.setText("Package");
                                        travelTxt.setText("Rental");
                                        break;
                                    case "CTY":
                                        travelTxt.setText("City");
                                        break;
                                    case "OST":
                                        travelTxt.setText("Outstation");
                                        break;
                                }

                                book_id.setText(prefix_+book_id_);
                                vtype.setText(vtype_);
                                start.setText(start_);
                                dest.setText(dest_);
                                date.setText(date_);

                            } catch (JSONException e) {
                                Log.i("Error on catch.....", Objects.requireNonNull(e.getMessage()));
                                e.printStackTrace();
                            }


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
        if (item.getItemId() == R.id.actionsearch) {
            init();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void cancel_btn(View view) {

        builder = new AlertDialog.Builder(this);

        //Setting message manually and performing action on button click
        builder.setMessage("Do you really want to cancel your ride ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        cancel_ride();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Confirmation");
        alert.show();
    }

    private void cancel_ride() {
        loading = ProgressDialog.show(this, "Cancelling your rides", "Please wait", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CANCEL_RIDE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "Responce..."+response);
                loading.dismiss();
                if (response.equals("succeses"))
                {
                    prefsManager.setTravel_type("");
                    Toast.makeText(YourRides.this, "Cancelled successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.i("Error", "volley response error");
                Toast.makeText(getApplicationContext(), "Responce error failed   " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cus_id", userid);
                params.put("book_id", book_id_);
                params.put("prefix", prefix_);
                return params;
            }
        };

        //inseting into  the iteluser table
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void status_btn(View view) {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.city_booking_confirm, null);
        final TextView basefareTxt = alertLayout.findViewById(R.id.baseFare);
        final TextView durationTxt = alertLayout.findViewById(R.id.duration);
        final TextView distanceTxt = alertLayout.findViewById(R.id.distance);

        loading = ProgressDialog.show(this, "Getting your rides", "Please wait", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.RENTAL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loading.dismiss();
//                Log.i("Responce......aoutside", response);
//                if (!response.equals("")) {
//                    Log.i("Responce....ain", response);
//                    try {
//                        JSONArray arr = new JSONArray(response);
//                        JSONObject jb = arr.getJSONObject(0);
//
//                        fare = jb.getString("base_fare");
//                        fare1 = jb.getString("per_hr");
//                        fare2 = jb.getString("per_km");
//
//                        Log.i("fare.......", fare);
//                        Log.i("per km.......", fare2);
//                        Log.i("per hr.......", fare1);
//



//        basefareTxt.setText(base_fare);
//        durationTxt.setText(duration);
//        distanceTxt.setText(distanceto);

//
//                    } catch (JSONException e) {
//                        Log.i("Error on catch.....", e.getMessage());
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.i("Responce.............", response);
//                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
//                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.i("Error", "volley response error");
                Toast.makeText(getApplicationContext(), "Responce error failed   " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
//                params.put("USER_ID", userid);
//                params.put("PACKAGE", packageid);
//                params.put("V_TYPE", v_type);
                return params;
            }
        };

        //inseting into  the iteluser table
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Info");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
//                citybooking();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}
