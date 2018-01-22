package com.lotadata.moments.plugin;

import org.apache.cordova.*;

import java.lang.NullPointerException;
import java.lang.IllegalArgumentException;

import android.Manifest;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import com.lotadata.moments.MomentsClient;
import com.lotadata.moments.Moments;
import com.lotadata.moments.TrackingMode;

public class MomentsPlugin extends CordovaPlugin {
    private static final String TAG = "MomentsPlugin";

    private Moments momentsClient = null;

    // Location Permissions
    private static final int REQUEST_LOCATION = 2342;
    public static String[] permissions = {
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private CallbackContext permissionsCallback;
    private String api_key;

    @Override
    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) throws JSONException {

        if (action.equals("initialize")) {
            api_key = data.getString(0);
            Log.i(TAG, "Initializing MomentsPlugin - In new Thread");
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    if (verifyPermissions(callbackContext)) {
                        momentsClient = MomentsClient.getInstance(cordova.getActivity(), api_key);
                        if (momentsClient != null) {
                            if (momentsClient.isConnected()) {
                                callbackContext.success("isConnected");
                            } else {
                                callbackContext.success("is Not Connected");
                            }
                        } else {
                            callbackContext.error("Error, permission OK but momentsClient still == null");
                        }
                    }
                }
            });
            return true;
        } else if (action.equals("recordEvent")) {
            if (momentsClient == null) {
                callbackContext.error("Not initialized!");
            } else {
                final String eventName = data.getString(0);
                if (data.length() > 1) {
                    final Double eventData = data.getDouble(1);
                    momentsClient.recordEvent(eventName, eventData);
                } else {
                    momentsClient.recordEvent(eventName);
                }
                callbackContext.success("Event recorded");
            }
            return true;
        } else if (action.equals("setTrackingMode")) {
            if (momentsClient == null) {
                callbackContext.error("Not initialized!");
            } else {
                final String trackingMode = data.getString(0);
                try {
                    TrackingMode mode = TrackingMode.valueOf(trackingMode);
                } catch (IllegalArgumentException err) {
                    callbackContext.error("Invalid trackingMode");
                } catch (NullPointerException err) {
                    callbackContext.error("trackingMode not specified");
                }
                momentsClient.setTrackingMode(mode);
                callbackContext.success("setTrackingMode OK");
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        if (permissionsCallback == null) {
            return;
        }

        if (requestCode != REQUEST_LOCATION) {
            return;
        }

        if (permissions != null && permissions.length > 0) {
            //Call checkPermission again to verify
            if (!hasAllPermissions()) {
                permissionsCallback.error("Error, ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permissions not granted");
            } else {
                if (momentsClient == null) {
                    momentsClient = MomentsClient.getInstance(cordova.getActivity(), api_key);
                }
                if (momentsClient != null) {
                    if (momentsClient.isConnected()) {
                        permissionsCallback.success("isConnected");
                    } else {
                        permissionsCallback.success("is Not Connected");
                    }
                } else {
                    permissionsCallback.error("Error, permission OK but momentsClient still == null");
                }
            }
        } else {
            permissionsCallback.error("Unknown error with permissions");
        }
        permissionsCallback = null;
    }

    private boolean hasAllPermissions() {
        for (String permission : permissions) {
            if(!cordova.hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    public boolean verifyPermissions(final CallbackContext callbackContext) {
        // Check if we have the permissions
        // and if we don't prompt the user
        // Return true if the permissions are granted. Else returns false and the authorization or not arrives on
        // call to method onRequestPermissionResult
        if (!hasAllPermissions()) {
            Log.i(TAG, "Asking for permissions " + permissions[0] + " and " + permissions[1]);
            cordova.requestPermissions(this, REQUEST_LOCATION, permissions);
            permissionsCallback = callbackContext;
            return hasAllPermissions();
        } else {
            return true;
        }
    }

    public void onDestroy() {
        if (momentsClient != null) {
            momentsClient.disconnect();
        }
        super.onDestroy();
    }
}
