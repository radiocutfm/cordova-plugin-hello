package com.lotadata.moments.plugin;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import com.lotadata.moments.MomentsClient;
import com.lotadata.moments.Moments;

public class MomentsPlugin extends CordovaPlugin {
    private Moments momentsClient = null;

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("initialize")) {

            String api_key = data.getString(0);
            momentsClient = MomentsClient.getInstance(this.cordova.getActivity(), api_key);
            if (momentsClient != null) {
                callbackContext.success("Connected!");
            } else {
                callbackContext.error("Error, momentsClient == null");
            }
            return true;

        } else {
            
            return false;

        }
    }

    public void onDestroy() {
        if (momentsClient != null) {
            momentsClient.disconnect();
        }
        super.onDestroy();
    }
}
