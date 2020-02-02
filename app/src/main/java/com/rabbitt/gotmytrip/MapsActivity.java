package com.rabbitt.gotmytrip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.rabbitt.gotmytrip.BroadcastReciever.InternetBroadcast;
import com.rabbitt.gotmytrip.CityPackage.cityBottomsheet;
import com.rabbitt.gotmytrip.MapPackage.CustomMapFragment;
import com.rabbitt.gotmytrip.MapPackage.MapWrapperLayout;
import com.rabbitt.gotmytrip.OutstationPackage.outstationBottomSheet;
import com.rabbitt.gotmytrip.PrefsManager.PrefsManager;
import com.rabbitt.gotmytrip.RentalPackage.BookBottomSheet;
import com.rabbitt.gotmytrip.YourRide.YourRides;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;

public class MapsActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, MapWrapperLayout.OnDragListener, GoogleMap.OnCameraMoveListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, BookBottomSheet.BottomSheetListener, NavigationView.OnNavigationItemSelectedListener {

     GoogleMap mMap;

    //Request Codes
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String TAG = "MapActivity";

    TextView pickupLocTxt, dropLocTxt;
    Button rent_button, city_button, outstation_button;
    Button prime, suv;
    LinearLayout bottomNavigationView;
    LinearLayout travel_type;
    int trance = 0;
    boolean isUp;

    //Type Variable says about rent or city or out
    String type;
    ViewGroup transitionsContainer;
    GoogleApiClient googleApiClient;
    LocationManager locationManager;
    Boolean ren = false, cit = false, out = false;
    LatLng origin, dest;
    Bitmap bitmap, bitmap1;
    CustomMapFragment mCustomMapFragment;
    InternetBroadcast receiver;
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        Toolbar toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (!checkPermission()) {
            requestPermission();
        }

        //denote user visit this page
        PrefsManager prefsManager = new PrefsManager(getApplicationContext());
        prefsManager.setFirstTimeLaunch(true);

        //Marker Initializing
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
        bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.marker1);

        //Initilize UI
        initializeUI();
        checkInternetConnectivity();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.i(TAG, "run: ");
//                if (!checkPermission()) {
//                    requestPermission();
//                }
//            }
//        }).start();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        turnOnGPS();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void checkInternetConnectivity() {
        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new InternetBroadcast();
        registerReceiver(receiver, filter);
    }

    private void initializeUI() {

        try {
//      Loading map
            Log.i(TAG, "initializeUI: ");
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

//      Ui initilization
        dropLocTxt = findViewById(R.id.dropLocation);
        pickupLocTxt = findViewById(R.id.pickupLocation);
        prime = findViewById(R.id.prime);
        suv = findViewById(R.id.suv);

//      First time view
        dropLocTxt.setVisibility(View.GONE);
        travel_type = findViewById(R.id.btn_nav_bar);
        rent_button = findViewById(R.id.rental);
        city_button = findViewById(R.id.city);
        outstation_button = findViewById(R.id.outstation);
        bottomNavigationView = findViewById(R.id.v_type);
        transitionsContainer = findViewById(R.id.transitions_container);

//      onclick listener
        suv.setOnClickListener(this);
        prime.setOnClickListener(this);
    }

    private void initilizeMap() {
        Log.i(TAG, "initilizeMap: ");
        if (mMap == null) {
            Log.i(TAG, "initilizeMap: Inside");
            mCustomMapFragment = ((CustomMapFragment) getFragmentManager().findFragmentById(R.id.map));
            mCustomMapFragment.setOnDragListener(MapsActivity.this);
            mCustomMapFragment.getMapAsync(this);

            // check if map is created successfully or not
            if (mMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }

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
                Log.i(TAG, "updateLocation: "+addresses);
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
                    origin = centerLatLng;
                } else {
                    dropLocTxt.setText(completeAddress);
                    dest = centerLatLng;
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady: ");
        this.mMap = googleMap;
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 11));
        }

        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
//Says about the Visibility of Cardview & Recycler View
                if (isUp) {
                    isUp = false;
                    slideDown(bottomNavigationView);
                }
                if (travel_type.getVisibility() == View.VISIBLE) {
                    travel_type.setVisibility(View.GONE);
                }
            }
        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (travel_type.getVisibility() == View.GONE)
                    travel_type.setVisibility(View.VISIBLE);
            }
        });
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
        trance = 0;
        mCustomMapFragment.getMapWrapperLayout().setCurrentImage(bitmap);
        slideDown(bottomNavigationView);
        if (!pickupLocTxt.getText().equals("")) {
            Toast.makeText(this, pickupLocTxt.getText().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void Searchdrop(View view) {
        trance = 1;
        mCustomMapFragment.getMapWrapperLayout().setCurrentImage(bitmap1);
        slideDown(bottomNavigationView);
        if (!dropLocTxt.getText().equals("")) {
            Toast.makeText(this, dropLocTxt.getText().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onSlideViewButtonClick(View view) {

        if (isUp) {

            switch (view.getId()) {
                case R.id.rental:
                    rent_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    type = "rental";
                    dropVisiblity(type);
                    ren = true;
                    cit = false;
                    out = false;
                    slideDown(bottomNavigationView);
                    break;
                case R.id.city:
                    city_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    type = "city";
                    dropVisiblity(type);
                    ren = false;
                    cit = true;
                    out = false;
                    slideDown(bottomNavigationView);
                    break;
                case R.id.outstation:
                    outstation_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    type = "outstation";
                    dropVisiblity(type);
                    ren = false;
                    cit = false;
                    out = true;
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
                    ren = true;
                    cit = false;
                    out = false;
                    break;

                case R.id.city:
                    city_button.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    rent_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    outstation_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    type = "city";
                    dropVisiblity(type);
                    ren = false;
                    cit = true;
                    out = false;

                    break;

                case R.id.outstation:
                    outstation_button.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    city_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    rent_button.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                    type = "outstation";
                    dropVisiblity(type);
                    ren = false;
                    cit = false;
                    out = true;
                    break;
            }
        }
        isUp = !isUp;
    }


    private void slideDown(View view) {
        view.setVisibility(View.GONE);
        isUp = false;
    }

    private void dropVisiblity(String type) {
        switch (type) {
            case "rental":
                if (dropLocTxt.getVisibility() == View.VISIBLE)
                    dropLocTxt.setVisibility(View.GONE);
                break;
            case "city":
            case "outstation":
                if (dropLocTxt.getVisibility() == View.GONE)
                    dropLocTxt.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.prime:

                if (ren){
                    if (pickupLocTxt.getText().toString().equals("")) {
                        Toast.makeText(this, "Please select the valid location", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    BookBottomSheet bottomSheet1 = new BookBottomSheet();
                    Bundle bundle = new Bundle();
                    bundle.putString("pickn", pickupLocTxt.getText().toString());
                    bundle.putString("ori_lat", String.valueOf(origin.latitude));
                    bundle.putString("ori_lng", String.valueOf(origin.longitude));
                    bundle.putString("vehicle", "Prime");
                    bundle.putString("travel_type", type);
                    bundle.putString("base_fare", "399");
                    bottomSheet1.setArguments(bundle);
                    bottomSheet1.show(getSupportFragmentManager(), "exampleBottomSheet");
                }
                else if (cit)
                {
                    if (pickupLocTxt.getText().toString().equals("") || dropLocTxt.getText().toString().equals("")) {
                        Toast.makeText(this, "Please select the valid location", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cityBottomsheet bottomSheet1 = new cityBottomsheet();
                    Bundle bundle = new Bundle();
                    bundle.putString("pickn", pickupLocTxt.getText().toString());
                    bundle.putString("dropn", dropLocTxt.getText().toString());
                    bundle.putString("base_fare", "150");
                    bundle.putString("vehicle", "Prime");
                    bundle.putString("travel_type", type);
                    bundle.putString("ori_lat", String.valueOf(origin.latitude));
                    bundle.putString("ori_lng", String.valueOf(origin.longitude));
                    bundle.putString("dest_lat", String.valueOf(dest.latitude));
                    bundle.putString("dest_lng", String.valueOf(dest.longitude));
                    bottomSheet1.setArguments(bundle);
                    bottomSheet1.show(getSupportFragmentManager(), "exampleBottomSheet");
                }
                else {
                    if (pickupLocTxt.getText().toString().equals("") || dropLocTxt.getText().toString().equals("")) {
                        Toast.makeText(this, "Please select the valid location", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    outstationBottomSheet bottomSheet1 = new outstationBottomSheet();
                    Bundle bundle = new Bundle();
                    bundle.putString("pickn", pickupLocTxt.getText().toString());
                    bundle.putString("dropn", dropLocTxt.getText().toString());
                    bundle.putString("vehicle", "Prime");
                    bundle.putString("travel_type", type);
                    bundle.putString("ori_lat", String.valueOf(origin.latitude));
                    bundle.putString("ori_lng", String.valueOf(origin.longitude));
                    bundle.putString("dest_lat", String.valueOf(dest.latitude));
                    bundle.putString("dest_lng", String.valueOf(dest.longitude));
                    bottomSheet1.setArguments(bundle);
                    bottomSheet1.show(getSupportFragmentManager(), "exampleBottomSheet");
                }
                break;

            case R.id.suv:

                if (ren){
                    BookBottomSheet bottomSheet2 = new BookBottomSheet();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("pickn", pickupLocTxt.getText().toString());
                    bundle1.putString("vehicle", "SUV");
                    bundle1.putString("travel_type", type);
                    bundle1.putString("base_fare", "599");
                    bundle1.putString("ori_lat", String.valueOf(origin.latitude));
                    bundle1.putString("ori_lng", String.valueOf(origin.longitude));
                    bottomSheet2.setArguments(bundle1);
                    bottomSheet2.show(getSupportFragmentManager(), "exampleBottomSheet");
                }
                else if (cit)
                {
                    cityBottomsheet bottomSheet2 = new cityBottomsheet();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("pickn", pickupLocTxt.getText().toString());
                    bundle1.putString("dropn", dropLocTxt.getText().toString());
                    bundle1.putString("base_fare", "200");
                    bundle1.putString("vehicle", "SUV");
                    bundle1.putString("travel_type", type);
                    bundle1.putString("ori_lat", String.valueOf(origin.latitude));
                    bundle1.putString("ori_lng", String.valueOf(origin.longitude));
                    bundle1.putString("dest_lat", String.valueOf(dest.latitude));
                    bundle1.putString("dest_lng", String.valueOf(dest.longitude));
                    bottomSheet2.setArguments(bundle1);
                    bottomSheet2.show(getSupportFragmentManager(), "exampleBottomSheet");
                }
                else {
                    outstationBottomSheet bottomSheet2 = new outstationBottomSheet();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("pickn", pickupLocTxt.getText().toString());
                    bundle1.putString("dropn", dropLocTxt.getText().toString());
                    bundle1.putString("vehicle", "SUV");
                    bundle1.putString("travel_type", type);
                    bundle1.putString("ori_lat", String.valueOf(origin.latitude));
                    bundle1.putString("ori_lng", String.valueOf(origin.longitude));
                    bundle1.putString("dest_lat", String.valueOf(dest.latitude));
                    bundle1.putString("dest_lng", String.valueOf(dest.longitude));
                    bottomSheet2.setArguments(bundle1);
                    bottomSheet2.show(getSupportFragmentManager(), "exampleBottomSheet");
                }

                break;
        }
    }
    private void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        isUp = true;
    }

    @Override
    public void onLocationChanged(Location location) {
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //move map camera
        Log.i(TAG, "onLocationChanged: "+latLng.toString());
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("Latitude","status");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onButtonClicked(String text) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_book:
//                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_your_rides:
                startActivity(new Intent(this, YourRides.class));
                break;
            case R.id.nav_about:
//                Toast.makeText(this, "3", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
            case R.id.nav_feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
//            case R.id.nav_support:
//                Toast.makeText(this, "5", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.nav_terms:
                startActivity(new Intent(this, TermsActivity.class));
                break;
                default:
                    break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    protected void onDestroy() {
        try {
            if (receiver != null)
                unregisterReceiver(receiver);
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }
}

//                    case "Auto":
//                        cityBottomsheet bottomSheet = new cityBottomsheet();
//                        Bundle bundle0 = new Bundle();
//                        bundle0.putString("pickn", pickupLocTxt.getText().toString());
//                        bundle0.putString("dropn", dropLocTxt.getText().toString());
//                        bundle0.putString("vehicle", "Auto");
//                        bundle0.putString("travel_type", type);
//                        bundle0.putString("base_fare", "35");
//                        bottomSheet.setArguments(bundle0);
//                        bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
//                        break;
