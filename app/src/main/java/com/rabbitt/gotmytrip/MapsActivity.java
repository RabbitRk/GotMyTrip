package com.rabbitt.gotmytrip;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rabbitt.gotmytrip.RentalPackage.BookBottomSheet;
import com.rabbitt.gotmytrip.CityPackage.cityBottomsheet;
import com.rabbitt.gotmytrip.MapPackage.CustomMapFragment;
import com.rabbitt.gotmytrip.MapPackage.MapWrapperLayout;
import com.rabbitt.gotmytrip.PrefsManager.PrefsManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, MapWrapperLayout.OnDragListener, GoogleMap.OnCameraMoveListener {

    private static final String TAG = "MainActivity";
    GoogleMap mMap;

    //Request Codes
    private static final int PERMISSION_REQUEST_CODE = 200;
    TextView pickupLocTxt, dropLocTxt;
    Button rent_button, city_button, outstation_button;
    BottomNavigationView bottomNavigationView;
    LinearLayout travel_type;
    int trance = 0;
    boolean isUp;
    //Type Variable says about rent or city or out
    String type;
//    View myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        //denote user visit this page
        PrefsManager prefsManager = new PrefsManager(getApplicationContext());
        prefsManager.setFirstTimeLaunch(true);

        //Initilize UI
        initializeUI();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: ");
                if (!checkPermission()) {
                    requestPermission();
                }
            }
        }).start();

        turnOnGPS();
    }

    private void initializeUI() {

        try {
            // Loading map
            Log.i(TAG, "initializeUI: ");
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

//        Ui initilization
        dropLocTxt = findViewById(R.id.dropLocation);
        pickupLocTxt = findViewById(R.id.pickupLocation);

//        First time view
        dropLocTxt.setVisibility(View.GONE);

        bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setVisibility(View.GONE);
//        ridelater = findViewById(R.id.button3);
        travel_type = findViewById(R.id.btn_nav_bar);
        rent_button = findViewById(R.id.rental);
        city_button = findViewById(R.id.city);
        outstation_button = findViewById(R.id.outstation);
    }

    private void initilizeMap() {
        Log.i(TAG, "initilizeMap: ");
        if (mMap == null) {
            CustomMapFragment mCustomMapFragment = ((CustomMapFragment) getFragmentManager().findFragmentById(R.id.map));
            mCustomMapFragment.setOnDragListener(MapsActivity.this);
            mCustomMapFragment.getMapAsync(this);

            // check if map is created successfully or not
            if (mMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
            mCustomMapFragment.getMapWrapperLayout().setCurrentImage(bitmap);
        }
    }

    @Override
    public void onDrag(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            Projection projection = mMap != null ? mMap.getProjection()
                    : null;
            if (projection != null) {
//                get center position of the screen and get latlng on that point
                LatLng centerLatLng = mMap.getCameraPosition().target;
                updateLocation(centerLatLng);
            }
        }
    }

    private void updateLocation(LatLng centerLatLng) {
        Log.i(TAG, "updateLocation: ");
        if (centerLatLng != null) {
            Geocoder geocoder = new Geocoder(MapsActivity.this,
                    Locale.getDefault());

            List<Address> addresses = new ArrayList<>();
            try {
                addresses = geocoder.getFromLocation(centerLatLng.latitude,
                        centerLatLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {

                String addressIndex0 = addresses
                        .get(0).getAddressLine(0);
                String addressIndex1 = addresses
                        .get(0).getAddressLine(1);
                String addressIndex2 = addresses
                        .get(0).getAddressLine(2);
                String addressIndex3 = addresses
                        .get(0).getAddressLine(3);

                String completeAddress = addressIndex0 + "," + addressIndex1;

                if (addressIndex2 != null) {
                    completeAddress += "," + addressIndex2;
                }
                if (addressIndex3 != null) {
                    completeAddress += "," + addressIndex3;
                }

                if (trance == 0) {
                    pickupLocTxt.setText(completeAddress);
                } else {
                    dropLocTxt.setText(completeAddress);
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady: ");
        this.mMap = googleMap;
    }

    private void turnOnGPS() {

        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            Toast.makeText(this, "Gps already enabled", Toast.LENGTH_SHORT).show();
        }

        if (!hasGPSDevice(this)) {
            Toast.makeText(this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }

        if (manager != null) {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
                Log.e("keshav", "Gps already enabled");
                Toast.makeText(this, "Gps not enabled", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("keshav", "Gps already enabled");
                Toast.makeText(this, "Gps already enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();

        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    //  Permission Checking Area-Start
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), TELEPHONY_SERVICE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NETWORK_STATE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, INTERNET, TELEPHONY_SERVICE, ACCESS_NETWORK_STATE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                    showMessageOKCancel(
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, INTERNET, ACCESS_NETWORK_STATE, TELEPHONY_SERVICE},
                                            PERMISSION_REQUEST_CODE);
                                }
                            });
                }
            }
        }
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(MapsActivity.this)
                .setMessage("You need to allow access to all the permissions")
                .setPositiveButton("OK", onClickListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();

    }

    @Override
    public void onCameraMove() {
        LatLng center = mMap.getCameraPosition().target;
        Log.i(TAG, "onCameraMove: " + center);
    }

//  Permission Checking Area-End

    public void Searchpickup(View view) {
        Toast.makeText(this, "pick up location", Toast.LENGTH_SHORT).show();
    }

    public void Searchdrop(View view) {
        Toast.makeText(this, "drop location", Toast.LENGTH_SHORT).show();
    }

    public void onSlideViewButtonClick(View view) {

        if (isUp) {

            switch (view.getId()) {
                case R.id.rental:
                    rent_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    type = "rental";
                    dropVisiblity(type);
                    getRentalnavigation(type);
                    slideDown(bottomNavigationView);
                    break;
                case R.id.city:
                    city_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    type = "city";
                    dropVisiblity(type);
                    getCitynavigation(type);
                    slideDown(bottomNavigationView);
                    break;
                case R.id.outstation:
                    outstation_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    type = "outstation";
                    dropVisiblity(type);
                    getOutstation(type);
                    slideDown(bottomNavigationView);
                    break;
            }

        } else {
            slideUp(bottomNavigationView);
            switch (view.getId()) {
                case R.id.rental:

                    rent_button.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    city_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    outstation_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    type = "rental";
                    dropVisiblity(type);
                    getRentalnavigation(type);
                    break;

                case R.id.city:

                    city_button.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    rent_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    outstation_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    type = "city";
                    dropVisiblity(type);
                    getCitynavigation(type);
                    break;

                case R.id.outstation:

                    outstation_button.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    city_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    rent_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    type = "outstation";
                    dropVisiblity(type);
                    getOutstation(type);
                    break;
            }

        }
        isUp = !isUp;
    }

    private void getOutstation(String type) {

    }

    private void slideDown(View view) {
        bottomNavigationView.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getHeight());
        animate.setDuration(500);
        view.startAnimation(animate);
    }

    private void dropVisiblity(String type) {
        switch (type) {
            case "rental":
                if (dropLocTxt.getVisibility() == View.VISIBLE)
                    dropLocTxt.setVisibility(View.GONE);
                break;
            case "city":
                if (dropLocTxt.getVisibility() == View.GONE)
                    dropLocTxt.setVisibility(View.VISIBLE);
                break;
            case "outstation":
                if (dropLocTxt.getVisibility() == View.GONE)
                    dropLocTxt.setVisibility(View.VISIBLE);
                break;
        }
    }


    public void disableAuto(boolean val) {
        if (val)
            bottomNavigationView.getMenu().getItem(0).setVisible(true);
        else
            bottomNavigationView.getMenu().getItem(0).setVisible(false);
    }


    private void getCitynavigation(final String type) {

        Log.i("my_tag", "Welcome");
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Log.i("my_tag", "Welcome2");
                switch (menuItem.getTitle().toString()) {
                    case "Auto":
                        cityBottomsheet bottomSheet = new cityBottomsheet();
                        Bundle bundle0 = new Bundle();
                        bundle0.putString("pickn", pickupLocTxt.getText().toString());
                        bundle0.putString("dropn", dropLocTxt.getText().toString());
                        bundle0.putString("vehicle", "Auto");
                        bundle0.putString("travel_type", type);
                        bundle0.putString("base_fare", "35");
                        bottomSheet.setArguments(bundle0);
                        bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
                        break;

                    case "Prime":
                        cityBottomsheet bottomSheet1 = new cityBottomsheet();
                        Bundle bundle = new Bundle();
                        bundle.putString("pickn", pickupLocTxt.getText().toString());
                        bundle.putString("dropn", dropLocTxt.getText().toString());
                        bundle.putString("base_fare", "150");
                        bundle.putString("vehicle", "Prime");
                        bundle.putString("travel_type", type);
                        bottomSheet1.setArguments(bundle);
                        bottomSheet1.show(getSupportFragmentManager(), "exampleBottomSheet");
                        break;

                    case "SUV":

                        cityBottomsheet bottomSheet2 = new cityBottomsheet();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("pickn", pickupLocTxt.getText().toString());
                        bundle1.putString("dropn", dropLocTxt.getText().toString());
                        bundle1.putString("base_fare", "200");
                        bundle1.putString("vehicle", "SUV");
                        bundle1.putString("travel_type", type);
                        bottomSheet2.setArguments(bundle1);
                        bottomSheet2.show(getSupportFragmentManager(), "exampleBottomSheet");
                        break;
                }
                return true;
            }
        });
    }


    private void getRentalnavigation(String typeof) {

        Log.i("my_tag", "Welcome");
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Log.i("my_tag", "Welcome2");
                switch (menuItem.getTitle().toString()) {
                    case "Auto":
                        AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                        alertDialog.setMessage("Auto is not provided for Rental");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        break;
                    case "Prime":
                        BookBottomSheet bottomSheet1 = new BookBottomSheet();
                        Bundle bundle = new Bundle();
                        bundle.putString("pickn", pickupLocTxt.getText().toString());
                        bundle.putString("vehicle", "Prime");
                        bundle.putString("travel_type", type);
                        bundle.putString("base_fare", "399");
                        bottomSheet1.setArguments(bundle);
                        bottomSheet1.show(getSupportFragmentManager(), "exampleBottomSheet");
                        break;

                    case "SUV":
                        BookBottomSheet bottomSheet2 = new BookBottomSheet();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("pickn", pickupLocTxt.getText().toString());
                        bundle1.putString("vehicle", "SUV");
                        bundle1.putString("travel_type", type);
                        bundle1.putString("base_fare", "599");
                        bottomSheet2.setArguments(bundle1);
                        bottomSheet2.show(getSupportFragmentManager(), "exampleBottomSheet");
                        break;
                }
                return true;
            }
        });
    }

    private void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

}