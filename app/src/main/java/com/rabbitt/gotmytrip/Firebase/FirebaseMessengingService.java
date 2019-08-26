package com.rabbitt.gotmytrip.Firebase;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.rabbitt.gotmytrip.Config;

public class FirebaseMessengingService extends FirebaseMessagingService {

    String TAG = "firebase";
    Bitmap bitmap;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.i("remote", "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
            } catch (Exception e) {
                Log.e("remote", "Exception: " + e.getMessage());
            }
        }
    }


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token);
        editor.apply();
        editor.commit();
    }
}