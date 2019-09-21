package com.rabbitt.gotmytrip.RentalPackage;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rabbitt.gotmytrip.Config;
import com.rabbitt.gotmytrip.DBhelper.dbHelper;
import com.rabbitt.gotmytrip.R;
import com.rabbitt.gotmytrip.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.rabbitt.gotmytrip.PrefsManager.PrefsManager.USER_NAME;
import static com.rabbitt.gotmytrip.PrefsManager.PrefsManager.USER_PREFS;

public class RentalActivity extends AppCompatActivity {

    RecyclerView packView;
    Boolean check_time = false, check_date = false;
    ArrayList<String> packages = new ArrayList<>(Arrays.asList("1 hr - 15 km", "2 hrs - 30 km", "4 hrs - 40 km", "6 hrs - 60 km", "8 hrs - 80 km", "10 hrs - 100 km",
            "12 hrs - 120 km"));
    String pickupLocation, dateon, timeat;
    String userid = "", v_type = "";
    //    ArrayList<String> base_fare_p = new ArrayList<>(Arrays.asList("399", "599", "899", "1299", "1699","1999", "2299"));
    String fare;
    String fare1;
    String fare2;
    TextView pickupLocTxt, dateonTxt, timeatTxt, changeval, fareTxt, per_kmTxt, per_hrTxt;
    ListView listView;
    String packageid;
    dbHelper yourrides;
    String datetime;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental);

        Toolbar toolbar = findViewById(R.id.toolbar);
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


        init();

    }

    private void init() {
        pickupLocTxt = findViewById(R.id.rentalpickup);
        dateonTxt = findViewById(R.id.dateon);
        timeatTxt = findViewById(R.id.timeat);
        changeval = findViewById(R.id.changedate);
        per_hrTxt = findViewById(R.id.per_hr);
        per_kmTxt = findViewById(R.id.per_km);
        fareTxt = findViewById(R.id.fare);

        //listview initial
        listView = findViewById(R.id.packdetails);
        listView.setScrollBarSize(8);
        listView.setScrollContainer(false);
        //getting shared preferences
        SharedPreferences userpref;
        userpref = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        userid = userpref.getString(USER_NAME, "");

        //getting intent
        Intent intent = getIntent();
        pickupLocation = intent.getStringExtra("pick");
        dateon = intent.getStringExtra("date");
        timeat = intent.getStringExtra("time");
        v_type = intent.getStringExtra("v_type");

        //initializing textviews
        pickupLocTxt.setText(pickupLocation);
        dateonTxt.setText(dateon);
        timeatTxt.setText(timeat);

        //initialiseing databse
        yourrides = new dbHelper(this);

        switch (v_type) {
            case "Prime":
                v_type = "2";
                break;
            case "SUV":
                v_type = "3";
                break;
            default:
                break;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, packages);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //getting text from list onItem clickevent
                packageid = listView.getItemAtPosition(position).toString();

                switch (packageid) {
                    case "1 hr - 15 km":
                        packageid = "1";
                        break;
                    case "2 hrs - 30 km":
                        packageid = "2";
                        break;
                    case "4 hrs - 40 km":
                        packageid = "3";
                        break;
                    case "6 hrs - 60 km":
                        packageid = "4";
                        break;
                    case "8 hrs - 80 km":
                        packageid = "5";
                        break;
                    case "10 hrs - 100 km":
                        packageid = "6";
                        break;
                    case "12 hrs - 120 km":
                        packageid = "7";
                        break;
                    default:
                        break;
                }
                bookrental();
                Toast.makeText(RentalActivity.this, "You Selected " + listView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bookrental() {


//      datetime = datein.getText().toString()+" "+timeat.getText().toString();
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Please wait...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.RENTAL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progress.dismiss();

                Log.i("Responce......aoutside", response);

                if (!response.equals("")) {
                    Log.i("Responce....ain", response);
                    try {
                        JSONArray arr = new JSONArray(response);
                        JSONObject jb = arr.getJSONObject(0);

                        fare = jb.getString("base_fare");
                        fare1 = jb.getString("per_hr");
                        fare2 = jb.getString("per_km");

                        Log.i("fare.......", fare);
                        Log.i("per km.......", fare2);
                        Log.i("per hr.......", fare1);

                        confirmAlert(fare, fare1, fare2);

                    } catch (JSONException e) {
                        Log.i("Error on catch.....", e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Log.i("Responce.............", response);
                    Toast.makeText(getApplicationContext(), "Responce is  " + response, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Log.i("Error", "volley response error");
                Toast.makeText(getApplicationContext(), "Responce error failed   " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("USER_ID", userid);
                params.put("PACKAGE", packageid);
                params.put("V_TYPE", v_type);

                return params;

            }
        };

        //inseting into  the iteluser table
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void confirmAlert(String fare, String fare1, String fare2) {
        per_hrTxt.setText(fare1);
        per_kmTxt.setText(fare2);
        fareTxt.setText(fare);
    }


    // Opens Time and Date On Click
    public void timeChange(View view) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        final TimePickerDialog timePickerDialog2 = new TimePickerDialog(RentalActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

//                        SimpleDateFormat timeat= new SimpleDateFormat("HH:mm");

                        timeatTxt.setText(hourOfDay + ":" + minute);
                    }

                }, mHour, mMinute, false);
        timePickerDialog2.show();

        final DatePickerDialog datePickerDialog2 = new DatePickerDialog(RentalActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

//                        SimpleDateFormat dateof = new SimpleDateFormat("dd-MM-yyyy");
                        dateonTxt.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }

                }, mYear, mMonth, mDay);

        datePickerDialog2.show();

    }

    public void getPackage(View view) {
        TextView value = view.findViewById(R.id.rentalpickup);//Changed for Temporary use   -Naveen
        String val = value.getText().toString();
        Toast.makeText(this, "hello toast..." + val, Toast.LENGTH_SHORT).show();
    }

    public void confirmBooking(View view) {

        dateon = dateonTxt.getText().toString() + " " + timeatTxt.getText().toString();

        datetime = dateon + " " + timeat;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CUSTOMER_RENTAL_BOOK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("Responce......outside", response);

                if (response.equalsIgnoreCase("success")) {
                    Log.i("Responce....in", response);
                    Toast.makeText(getApplicationContext(), "Your booking placed", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("Responce.............", response);
                    Toast.makeText(getApplicationContext(), "Responce is  " + response, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                    yourRides();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error", "volley response error");
                Toast.makeText(getApplicationContext(), "Responce error failed   " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("CUS_ID", userid);
                params.put("BOOK_TIME", datetime);
                params.put("ORIGIN", pickupLocation);
                params.put("TRAVEL_TYPE", v_type);
                params.put("PACKAGE_ID", packageid);
                params.put("FARE", fare);

//                Log.i("LNG", traveltype);
//                Log.i("LNG", loc);
//                Log.i("LNG", datetime);
//                Log.i("LNG", packagei);

                return params;

            }
        };

        //inseting into  the iteluser table
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void yourRides() {
        yourrides.insertdata("1", datetime, "Rental", v_type, pickupLocation, "");
        Log.i("value", "inserted");
    }


}