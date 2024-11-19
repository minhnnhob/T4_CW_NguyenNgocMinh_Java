package com.example.yogau.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkUtils {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        Log.d(
                "NetworkUtils",
                "isNetworkAvailable: " + (networkInfo != null && networkInfo.isConnected())
        );
        return networkInfo != null && networkInfo.isConnected();
    }
}