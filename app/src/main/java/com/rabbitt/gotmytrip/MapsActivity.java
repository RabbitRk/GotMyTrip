package com.rabbitt.gotmytrip;

import android.app.Activity;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class MapsActivity extends Activity implements OnMapReadyCallback, MapWrapperLayout.OnDragListener, GoogleMap.OnCameraMoveListener {

    private static final String TAG = "MainActivity";
    GoogleMap mMap;
    //Request Codes
    private static final int PERMISSION_REQUEST_CODE = 200;
    TextView pickupLocTxt, dropLocTxt;
    Button rent_button, city_button, outstation_button;
    BottomNavigationView bottomNavigationView;
    LinearLayout travel_type;
    int trance = 0;

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
            CustomMapFragment mCustomMapFragment = ((CustomMapFragment) getFragmentManager()
                    .findFragmentById(R.id.map));
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

                if (trance == 0)
                {
                    pickupLocTxt.setText(completeAddress);
                }
                else
                {
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

        // Todo Location Already on  ... start
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            Toast.makeText(this, "Gps already enabled", Toast.LENGTH_SHORT).show();

        }
        // Todo Location Already on  ... end

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


//    Permission Checking Area-Start
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
        Log.i(TAG, "onCameraMove: "+center);
    }
//    Permission Checking Area-End
}