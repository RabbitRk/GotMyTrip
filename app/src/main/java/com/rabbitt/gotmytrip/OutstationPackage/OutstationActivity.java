package com.rabbitt.gotmytrip.OutstationPackage;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.rabbitt.gotmytrip.PrefsManager.PrefsManager.ID_KEY;
import static com.rabbitt.gotmytrip.PrefsManager.PrefsManager.USER_PREFS;

public class OutstationActivity extends AppCompatActivity {

    String pickupLocation, dropLocation, dateon, timeat;
    String oriLat, oriLng, destLat, destLng, travel_type;
    String total_fare, total_hrs, total_min, total_distance, exclusive_hr, per_km, day_allowance, night_allownance;

    String userid = "", v_type = "", v_type1 = "";
    String user_id;

    TextView pickupLocTxt, dateonTxt, returnChangedateTxt, timeatTxt, changeCurval, fareTxt, distanceTxt, durationTxt, dropLocTxt, exclusiveHrTxt, perKmTxt, dayAllowanceTxt, nightAllowanceTxt, totalHrsTxt, returndateTxt;
    dbHelper yourrides;
    String datetime;

    SharedPreferences userpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outstation_acitvity);

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

        //textview initialization
        pickupLocTxt = findViewById(R.id.cityPickup);
        dropLocTxt = findViewById(R.id.cityDrop);
        dateonTxt = findViewById(R.id.dateon);
        timeatTxt = findViewById(R.id.timeat);
        changeCurval = findViewById(R.id.changedate);
        distanceTxt = findViewById(R.id.distance);
        durationTxt = findViewById(R.id.duration);
        fareTxt = findViewById(R.id.fare);
        exclusiveHrTxt = findViewById(R.id.ex_hr);
        perKmTxt = findViewById(R.id.per_km);
        dayAllowanceTxt = findViewById(R.id.day_allowance);
        nightAllowanceTxt = findViewById(R.id.night_allowance);
        returndateTxt = findViewById(R.id.return_dateon);
        returnChangedateTxt = findViewById(R.id.return_changedate);

        SharedPreferences userpref;
        userpref = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        userid = userpref.getString(ID_KEY, "");

        //getting intent
        Intent intent = getIntent();
        pickupLocation = intent.getStringExtra("pick_up");
        dropLocation = intent.getStringExtra("drop");
        dateon = intent.getStringExtra("date");
        timeat = intent.getStringExtra("time");
        v_type = intent.getStringExtra("v_type");
        v_type1 = intent.getStringExtra("v_type");
        travel_type = intent.getStringExtra("travel_type");
        oriLat = intent.getStringExtra("ori_lat");
        oriLng = intent.getStringExtra("ori_lng");
        destLat = intent.getStringExtra("dest_lat");
        destLng = intent.getStringExtra("dest_lng");

        //initialiseing databse
        yourrides = new dbHelper(this);

        //initializing textviews
        pickupLocTxt.setText(pickupLocation);
        dropLocTxt.setText(dropLocation);
        dateonTxt.setText(dateon);
        timeatTxt.setText(timeat);

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

        getuserPrefs();
        getTravelDetails();
    }

    private void getTime() {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        final TimePickerDialog timePickerDialog2 = new TimePickerDialog(OutstationActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeof = new SimpleDateFormat("HH:mm");
                        timeatTxt.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }

                }, mHour, mMinute, false);
        timePickerDialog2.show();
    }

    private void getTravelDetails() {

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Please wait...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.OUTSTATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("Responce......outside", response);
                progress.dismiss();

                if (!response.equals("")) {
                    Log.i("Responce....in", response);
                    try {
                        JSONArray arr = new JSONArray(response);
                        JSONObject jb = arr.getJSONObject(0);
                        Log.i("Responce....try", response);

                        total_fare = jb.getString("f_fare");
                        total_hrs = jb.getString("t_hrs");
                        total_min = jb.getString("t_min");
                        total_distance = jb.getString("distance");
                        exclusive_hr = jb.getString("ex_hr");
                        per_km = jb.getString("per_km");
                        day_allowance = jb.getString("day");
                        night_allownance = jb.getString("night");

                        Log.i("totalLog", "val: " + total_distance);
                        Log.i("totalLog", total_hrs);
                        Log.i("totalLog", total_min);
                        Log.i("totalLog", per_km);

                        Toast.makeText(OutstationActivity.this, "hellow  " + total_distance, Toast.LENGTH_SHORT).show();
                        confirmAlert(total_fare, total_hrs, total_min, total_distance, exclusive_hr, per_km, day_allowance, night_allownance);

                    } catch (JSONException e) {
                        Log.i("Error on catch.....", "catch");
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

                params.put("OUTSTATION", "outstation");
                params.put("ORIGIN_LAT", oriLat);
                params.put("ORIGIN_LNG", oriLng);
                params.put("DESTINATION_LAT", destLat);
                params.put("DESTINATION_LNG", destLng);
                params.put("VEHICLE_TYPE", v_type);

                Log.i("outstation", oriLat);
                Log.i("outstation", oriLng);
                Log.i("outstation", destLat);
                Log.i("outstation", destLng);
                Log.i("outstation", v_type);

                return params;

            }
        };

        //inseting into  the iteluser table
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void confirmAlert(String total_fare, String total_hrs, String total_min, String total_distance, String exclusive_hr, String per_km, String day_allowance, String night_allownance) {
        String totalTime = total_hrs + "hr:" + total_min + "mins";

//        Log.i("textviewLog", total_distance);
//        Log.i("textviewLog", total_fare);
//        Log.i("textviewLog", total_hrs);
//        Log.i("textviewLog", total_min);
//        Log.i("textviewLog", exclusive_hr);
//        Log.i("textviewLog", per_km);
//        Log.i("textviewLog", totalTime);

        fareTxt.setText(total_fare);
        durationTxt.setText(totalTime);
        distanceTxt.setText(total_distance);
        exclusiveHrTxt.setText(exclusive_hr);
        perKmTxt.setText(per_km);
        dayAllowanceTxt.setText(day_allowance);
        nightAllowanceTxt.setText(night_allownance);
    }

    private void getuserPrefs() {
        userpref = getSharedPreferences(USER_PREFS, MODE_PRIVATE);

        user_id = userpref.getString(ID_KEY, "");
        Toast.makeText(this, "Uid: " + user_id, Toast.LENGTH_SHORT).show();
        if ("".equals(user_id)) {
            Toast.makeText(this, "User ID is not valid", Toast.LENGTH_SHORT).show();
        }

    }

    public void confirmBooking(View view) {

        final String oriLngLng = oriLat + ',' + oriLng;
        final String desLngLng = destLat + ',' + destLng;

        datetime = dateonTxt.getText().toString() + " " + timeatTxt.getText().toString();

        final String ret_date = returndateTxt.getText().toString();
        if (ret_date.equals("Return Date")) {
            Toast.makeText(this, "Please select the return Date", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CUSTOMER_OUTSTATION_BOOK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("Responce......outside", response);

                if (response.equals("failed")) {
                    Log.i("Responce.............", response);
                    Toast.makeText(getApplicationContext(), "Sorry!, Can't book your ride, right now !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Booked Successfully", Toast.LENGTH_SHORT).show();
                    yourRides(response);
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

                String datetime = dateonTxt.getText().toString() + " " + timeatTxt.getText().toString();

                params.put("CUS_ID", userid);
                params.put("BOOK_TIME", datetime);
                params.put("ORIGIN", pickupLocation);
                params.put("DESTINATION", dropLocation);
                params.put("BASE_FARE", total_fare);
                params.put("KMETER", total_distance);
                params.put("VEHICLE_ID", v_type);
                params.put("HOURS", total_hrs);
                params.put("RETURN_DATE", ret_date);
                params.put("ORI_LAT_LNG", oriLngLng);
                params.put("DES_LAT_LNG", desLngLng);

                Log.i("paramsVolley", pickupLocation);

                return params;

            }
        };

        //inseting into  the iteluser table
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void timeChange(View view) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        final TimePickerDialog timePickerDialog2 = new TimePickerDialog(OutstationActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        timeatTxt.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }

                }, mHour, mMinute, false);
        timePickerDialog2.show();

        final DatePickerDialog datePickerDialog2 = new DatePickerDialog(OutstationActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateonTxt.setText(String.format("%d-%d-%d", dayOfMonth, monthOfYear + 1, year));
                    }

                }, mYear, mMonth, mDay);

        datePickerDialog2.show();
    }

    public void timeChangeout(View view) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog2 = new DatePickerDialog(OutstationActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        returndateTxt.setText(String.format("%d-%d-%d", dayOfMonth, monthOfYear + 1, year));
                    }

                }, mYear, mMonth, mDay);

        datePickerDialog2.show();
    }

    private void yourRides(String response) {
        yourrides.insertdata(response+"OTS", datetime, "Outstation", v_type1, pickupLocation, dropLocation);
        Log.i("value", "inserted");
    }
}
