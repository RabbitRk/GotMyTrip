package com.rabbitt.gotmytrip.CityPackage;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rabbitt.gotmytrip.Config;
import com.rabbitt.gotmytrip.DBhelper.dbHelper;
import com.rabbitt.gotmytrip.PrefsManager.PrefsManager;
import com.rabbitt.gotmytrip.R;
import com.rabbitt.gotmytrip.VolleySingleton;
import com.rabbitt.gotmytrip.YourRides;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.rabbitt.gotmytrip.PrefsManager.PrefsManager.ID_KEY;
import static com.rabbitt.gotmytrip.PrefsManager.PrefsManager.USER_PREFS;

public class CityActivity extends AppCompatActivity {

    private static final String TAG = "CityActivity";
    String pickupLocation, dropLocation, dateon, timeat;
    String oriLat, oriLng, destLat, destLng, travel_type;
    String userid = "", v_type = "",  v_type1 = "";
    String base_fare;
    String distanceto;
    String duration;
    String user_id;
    TextView pickupLocTxt, dateonTxt, timeatTxt, changeval, fareTxt, distanceTxt, durationTxt, dropLocTxt;
    //    ListView listView;
    dbHelper yourrides;
    String datetime;
    SharedPreferences userpref;
    //    RecyclerView packView;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow); // your drawable
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Implemented by activity
            }
        });

        init();
    }

    private void init() {

//      textview initialization
        pickupLocTxt = findViewById(R.id.cityPickup);
        dropLocTxt = findViewById(R.id.cityDrop);
        dateonTxt = findViewById(R.id.dateon);
        timeatTxt = findViewById(R.id.timeat);
        changeval = findViewById(R.id.changedate);
        distanceTxt = findViewById(R.id.distance);
        durationTxt = findViewById(R.id.duration);
        fareTxt = findViewById(R.id.fare);

//      getting shared preferences
        SharedPreferences userpref;
        userpref = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        userid = userpref.getString(ID_KEY, "");
        Log.i(TAG, "init: " + userid);

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

        Log.i(TAG, "init: "+" "+oriLat+" "+pickupLocation);

        //initializing textviews
        pickupLocTxt.setText(pickupLocation);
        dropLocTxt.setText(dropLocation);
        dateonTxt.setText(dateon);
        timeatTxt.setText(timeat);

        //initialiseing database
        yourrides = new dbHelper(this);

        switch (v_type) {
            case "Auto":
                v_type = "1";
                break;
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
        getCurrentDateTime();
        getDetails();
    }

    public void timeChange(View view) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        final TimePickerDialog timePickerDialog2 = new TimePickerDialog(CityActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        timeatTxt.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }

                }, mHour, mMinute, false);
        timePickerDialog2.show();
        final DatePickerDialog datePickerDialog2 = new DatePickerDialog(CityActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dateonTxt.setText(String.format("%d-%d-%d", dayOfMonth, monthOfYear + 1, year));
//                                Toast.makeText(RentalView.this,dayOfMonth + "-" + (monthOfYear + 1) + "-" + year,Toast.LENGTH_SHORT).show();
                    }

                }, mYear, mMonth, mDay);


        datePickerDialog2.show();
    }

    private void getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dft = new SimpleDateFormat("HH:mm");
        String rideNow_date = df.format(c.getTime());
        String rideNow_time = dft.format(c.getTime());
        dateonTxt.setText(rideNow_date);
        timeatTxt.setText(rideNow_time);
    }

    private void getuserPrefs() {
        userpref = getSharedPreferences(USER_PREFS, MODE_PRIVATE);

        user_id = userpref.getString(ID_KEY, "");

        if ("".equals(user_id)) {
            Toast.makeText(this, "User ID is not valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDetails() {

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Please wait...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CITY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();

                if (!response.equals("failed")) {
                    Log.i("Responce.............", response);
                    try {
                        JSONArray arr = new JSONArray(response);
                        JSONObject jb = arr.getJSONObject(0);
                        distanceto = jb.getString("distance");
                        duration = jb.getString("duration");
                        base_fare = jb.getString("fare");

                        distanceTxt.setText(distanceto);
                        durationTxt.setText(duration);
                        fareTxt.setText(String.valueOf(base_fare));

                        Log.i("distance.......", distanceto);
                        Log.i("duration.......", duration);
                        Log.i("fare.......", base_fare);

                    } catch (JSONException ex) {
                        Log.i("Error on catch.....", Objects.requireNonNull(ex.getMessage()));
                        ex.printStackTrace();
                    }
                } else {
                    Log.i("Responce.............", response);
                    Toast.makeText(getApplicationContext(), "Distance is too short to book your ride..." + response, Toast.LENGTH_SHORT).show();
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

                params.put("ORIGIN_LAT", oriLat);
                params.put("ORIGIN_LNG", oriLng);
                params.put("DESTINATION_LAT", destLat);
                params.put("DESTINATION_LNG", destLng);
                params.put("VEHICLE_TYPE", v_type);

                Log.i("ORIGIN_LAT", oriLat);
                Log.i("ORIGIN_LNG", oriLng);
                Log.i("DESTINATION_LAT", destLat);
                Log.i("DESTINATION_LNG", destLng);
                Log.i("VEHICLE_TYPE", v_type);
                return params;
            }
        };

        //inseting into  the iteluser table
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void confirmBooking(View view) {
        reg();
    }

    private void reg() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.city_booking_confirm, null);
        final TextView basefareTxt = alertLayout.findViewById(R.id.baseFare);
        final TextView durationTxt = alertLayout.findViewById(R.id.duration);
        final TextView distanceTxt = alertLayout.findViewById(R.id.distance);
        alertLayout.setBackgroundColor(ContextCompat.getColor(this ,R.color.bgcolor));

        basefareTxt.setText(base_fare);
        durationTxt.setText(duration);
        distanceTxt.setText(distanceto);

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
                citybooking();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void citybooking() {

        datetime = dateonTxt.getText().toString() + " " + timeatTxt.getText().toString();
        final String oriLngLng = oriLat + ',' + oriLng;
        final String desLngLng = destLat + ',' + destLng;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CUSTOMER_CITY_BOOK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("Responce.............", response);

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
                params.put("CUS_ID", user_id);
                params.put("ORIGIN", pickupLocation);
                params.put("DESTINATION", dropLocation);
                params.put("BASE_FARE", base_fare);
                params.put("KMETER", distanceto);
                params.put("VEHICLE_ID", v_type);
                params.put("ORI_LAT_LNG", oriLngLng);
                params.put("DES_LAT_LNG", desLngLng);
                return params;
            }
        };

        //inseting into  the iteluser table
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void yourRides(String response) {
//        yourrides.insertdata(response+"CTY", datetime, "City", v_type1, pickupLocation, dropLocation);
        Log.i("value","inserted");
        PrefsManager prefsManager = new PrefsManager(this);
        prefsManager.setTravel_type("City");
        startActivity(new Intent(this, YourRides.class));
    }
}