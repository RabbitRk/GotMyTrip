package com.rabbitt.gotmytrip.CityPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rabbitt.gotmytrip.DBhelper.dbHelper;
import com.rabbitt.gotmytrip.R;

import static com.rabbitt.gotmytrip.PrefsManager.PrefsManager.USER_PREFS;

public class CityActivity extends AppCompatActivity {

    Boolean check_time = false, check_date = false;
    String pickupLocation, dropLocation, dateon, timeat;
    String oriLat, oriLng, destLat, destLng, travel_type;
    String userid = "", v_type = "";
    String base_fare;
    String distanceto;
    String duration;
    String user_id;
    TextView pickupLocTxt, dateonTxt, timeatTxt, changeval, fareTxt, distanceTxt, durationTxt, dropLocTxt;
    //    ListView listView;
    String packageid;
    dbHelper yourrides;
    String datetime;
    SharedPreferences userpref;
    //    RecyclerView packView;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

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

    }

    public void login(View view) {
    }

    public void timeChange(View view) {
    }
}
