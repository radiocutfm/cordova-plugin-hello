package com.lotadata.moments.plugin;

import org.apache.cordova.*;

import android.Manifest;

import org.json.JSONArray;
import org.json.JSONException;
import com.lotadata.moments.MomentsClient;
import com.lotadata.moments.Moments;

public class MomentsPlugin extends CordovaPlugin {
    private Moments momentsClient = null;

    // Location Permissions
    private static final int REQUEST_LOCATION = 1;
    public static String[] permissions = {
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
	};

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("initialize")) {
            String api_key = data.getString(0);
            if (!verifyPermissions()) {
                callbackContext.error("Error, needed permissions not granted");
            } else {
                momentsClient = MomentsClient.getInstance(this.cordova.getActivity(), api_key);
                if (momentsClient != null) {
                    if (momentsClient.isConnected()) {
                        callbackContext.success("isConnected - API_KEY: " + api_key);
                    } else {
                        callbackContext.success("is Not Connected - API_KEY: " + api_key);
                    }
                } else {
                    callbackContext.error("Error, momentsClient == null - api_key: " + api_key);
                }
            }
            return true;

        } else {
            
            return false;

        }
    }

    public boolean verifyPermissions() {
        // Check if we have write permission
        // and if we don't prompt the user
        if (!cordova.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            cordova.requestPermissions(this, REQUEST_LOCATION, permissions);
        }
        return cordova.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) && cordova.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public void onDestroy() {
        if (momentsClient != null) {
            momentsClient.disconnect();
        }
        super.onDestroy();
    }
}
