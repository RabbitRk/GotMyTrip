package com.rabbitt.gotmytrip;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rabbitt.gotmytrip.PrefsManager.PrefsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    EditText password, phone_number;
    String passTxt, phoneTxt;
    String PuserTxt, PemailTxt, getId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //ui initilization
        init();
    }

    private void init() {
        password = findViewById(R.id.confirm_pass);
        phone_number = findViewById(R.id.phonenumber);
    }

    public void create_account(View view) {
        Intent register = new Intent(this, RegisterActivity.class);
        startActivity(register);
    }

    public void login(View view) {
        passTxt = password.getText().toString().trim();
        phoneTxt = phone_number.getText().toString().trim();

        if (TextUtils.isEmpty(phoneTxt)) {
            phone_number.setError("Enter the phone number");
            phone_number.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(passTxt)) {
            password.setError("Enter the password");
            password.requestFocus();
            return;
        }

        //Displaying a progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Registering", "Please wait...", false, false);

        //Again creating the string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.USER_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Log.i(TAG, "Responce.............." + response);

                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject jb = arr.getJSONObject(0);
                            getId = jb.getString("id");
                            PuserTxt = jb.getString("name");
                            PemailTxt = jb.getString("email");

                            setPrefsdetails();
                            loginto();

                        } catch (JSONException e) {
                            Log.i(TAG, "Json error.............." + e.getMessage());
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
                params.put("passWord", passTxt);
                params.put("phoneNumber", phoneTxt);
                return params;
            }
        };

        //Adding request the the queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void loginto() {
        Intent mainA = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(mainA);
        finish();
        Log.i(TAG, "Hid.............." + getId);
    }

    private void setPrefsdetails() {

        PrefsManager prefsManager = new PrefsManager(this);
        prefsManager.userPreferences(getId, PuserTxt, phoneTxt, PemailTxt);
        Log.i(TAG, "set preference Hid.............." + getId);
    }

    public void forgot(View view) {
        Intent forgot = new Intent(this,Forgot_password.class);
        startActivity(forgot);
    }
}
