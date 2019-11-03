package com.rabbitt.gotmytrip;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OtpActivity extends AppCompatActivity {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static final String TAG = "MainActivity";
    String otpLocal = "", phoneTxt = "";
    EditText tv;
    String token;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        init();
        getToken();
    }

    private void getToken() {

        SharedPreferences sp;
        sp = getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
        token = sp.getString("token","");
        if (token.equals(""))
        {
            Toast.makeText(this, "Firebase token is not registered", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        Intent intent = getIntent();
        phoneTxt = intent.getStringExtra(RegisterActivity.PHONE_EXTRA);
        tv = findViewById(R.id.otpTxt);

        Log.i(TAG, "Phone checking........................" + phoneTxt);

        if (checkAndRequestPermissions()) {
            Log.i(TAG, "Inside the normal flow");
            // carry on the normal flow, as the case of  permissions  granted.
        }
    }

    private boolean checkAndRequestPermissions() {
        Log.i(TAG, "Request checking........................");

        int permissionSendMessage = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int receiveSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int readSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);                  ///suspicious  +>if
        int result4 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (result != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (result1 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (result4 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (result2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[0]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void otpVerification(View view) {
        //Getting the user entered otp from edittext
        otpLocal = tv.getText().toString().trim();

        //validations
        if (otpLocal.equals("") || otpLocal.length() < 4) {
            Toast.makeText(this, "Please enter the Got My Trip OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        //Displaying a progressbar
        loading = ProgressDialog.show(this, "Authenticating", "Please wait while we check the entered OTP", false, false);

        //Creating an string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.OTP_VERIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //if the server response is success
                        Log.i(TAG, "Response........................" + response);
                        if (response.equalsIgnoreCase("success")) {
                            //dismissing the progressbar
                            loading.dismiss();
                            //Starting a new activity

                            startActivity(new Intent(getApplicationContext(), MapsActivity.class));

                        } else {
                            loading.dismiss();
                            //Displaying a toast if the otp entered is wrong
                            Toast.makeText(getApplicationContext(), "Wrong OTP Please Try Again", Toast.LENGTH_LONG).show();
                        }
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
                params.put("cus_phone", phoneTxt);
                params.put("otp", otpLocal);
                params.put("token", token);
                return params;
            }
        };
        Log.i(TAG, "otp checking........................" + otpLocal);
        //Adding the request to the queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
