package com.rabbitt.gotmytrip;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Forgot_password extends AppCompatActivity {
    private static final String TAG = "Forgot Password";
    EditText email, phone_number;
    String emailTxt, phoneTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        init();
    }

    private void init() {
        email = findViewById(R.id.emailTxt);
        phone_number = findViewById(R.id.phoneTxt);
    }

    public void submit(View view) {
        emailTxt = email.getText().toString().trim();
        phoneTxt = phone_number.getText().toString().trim();

        if (TextUtils.isEmpty(phoneTxt)) {
            phone_number.setError("Enter the phone number");
            phone_number.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
            email.setError("Enter the valid Email ID");
            email.requestFocus();
            return;
        }

        //Displaying a progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Registering", "Please wait...", false, false);

        //Again creating the string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.FORGOT_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Log.i(TAG, "Responce.............." + response);

                        if (response.equals("success"))
                        {
                            loginto();
                        }
                        else {
                            Toast.makeText(Forgot_password.this, "Email ID or Phone number is not found....Please Register", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Log.i(TAG, "volley error.............................." + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Username or Phone number not found", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters to the request
                params.put("emailID", emailTxt);
                params.put("phoneNumber", phoneTxt);
                return params;
            }
        };

        //Adding request the the queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void loginto() {

        Button buttonConfirm;
        final EditText f_otp;
        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        @SuppressLint("InflateParams")
        View confirmDialog = li.inflate(R.layout.otp_dialog_confirm, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        buttonConfirm = confirmDialog.findViewById(R.id.buttonConfirm);
        f_otp = confirmDialog.findViewById(R.id.editTextOtp);

        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);
        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //Displaying the alert dialog
        alertDialog.setCancelable(false);
        alertDialog.show();

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                String otp = f_otp.getText().toString();
                validateOtp(otp);
            }
        });
    }

    private String getToken() {
        SharedPreferences sp;
        sp = getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
        String token = sp.getString("token","");
        if (token.equals(""))
        {
            Toast.makeText(this, "Firebase token is not registered", Toast.LENGTH_SHORT).show();
        }
        return token;
    }

    private void validateOtp(final String otp) {

        //validations
        if (otp.equals("") || otp.length() < 4) {
            Toast.makeText(this, "Please enter the Got My Trip OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        //Displaying a progressbar
        final ProgressDialog loading = ProgressDialog.show(this, "Authenticating", "Please wait while we check the entered OTP", false, false);

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
                params.put("otp", otp);
                params.put("token", getToken());
                return params;
            }
        };
        Log.i(TAG, "otp checking........................" + getToken());
        //Adding the request to the queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
