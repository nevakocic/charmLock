package com.airsig.plugin;

import android.content.SharedPreferences;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import com.airsig.webclientgui.ASActivityManager;
import com.airsig.webclientgui.ASManager;
import com.airsig.webclientgui.Const;

public class AirSigGUIPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        String userId = null;
        if (data.length() > 0) {
            userId = data.getString(0);
        }

        ASActivityManager caller = new ASActivityManager(cordova.getActivity().getApplicationContext(), userId);

        if (action.equals("showEntryActivity")) {
            caller.showEntryActivity();
            callbackContext.success("");
            return true;
        } else if (action.equals("showTrainingActivity")) {
            caller.showTrainingActivity();
            callbackContext.success("");
            return true;
        } else if (action.equals("showVerifyActivity")) {
            caller.showVerifyActivity();
            return true;
        } else if (action.equals("showTutorialActivity")) {
            caller.showTutorialActivity();
            callbackContext.success("");
            return true;
        } else if (action.equals("isSignatureValid")) {
            if (caller.isSignatureValid()) {
                callbackContext.success("");
                return true;
            }
            else {
                callbackContext.error("");
                return false;
            }
        } else if (action.equals("resetSignatureValid")) {
            if(caller.resetSignatureValid()) {
                callbackContext.success("Signature is reseted.");
                return true;
            }
            else {
                callbackContext.error("Signature is not reseted.");
                return false;
            }
        }
//        else {
//            ASManager manager = new ASManager(cordova.getActivity().getApplicationContext(), callbackContext, userId);
//            if (action.equals("deleteRecord")) {
//                manager.deleteRecord();
//                return true;
//            } else if (action.equals("resetSignature")) {
//                manager.resetSignature();
//                return true;
//            } else if (action.equals("startRecordSensor")) {
//                manager.startRecordSensor();
//                return true;
//            } else if (action.equals("completeRecordSensorToIdentifyAction")) {
//                manager.completeRecordSensorToIdentifyAction();
//                return true;
//            } else if (action.equals("completeRecordSensorToTrainAction")) {
//                manager.completeRecordSensorToTrainAction();
//                return true;
//            }
//        }

        return false;
    }
}
