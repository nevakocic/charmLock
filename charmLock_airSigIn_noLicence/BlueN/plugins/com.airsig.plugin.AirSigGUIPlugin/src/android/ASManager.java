package com.airsig.webclientgui;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.apache.cordova.*;

import com.airsig.webclient.ASEngine;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ssoko_000 on 13.4.2015..
 */
public class ASManager {
    Context mContext;
    CallbackContext mCallbackContext;

    /**
     * Get a valid license key from www.airsig.com.
     */
    private String mLicence = "";

    /**
     * mUserId was used to represent the different user. It shall be a specific identification. ex: user's e-mail address.
     */
    private String mUserId = "";

    private ASGui mASGui;
    private ASEngine mASEngine;

    private boolean mReadyToVerify = false;

    private float mFingerPositionX = ASGui.NOPOSITION;
    private float mFingerPositionY = ASGui.NOPOSITION;
    private ASEngine.ASStrength mStrength;
    private int mNumberOfSignatureStillNeed;
    private Handler mHandler = new Handler();

    /**
     * mActionIndex is the index number of each air signature. It shall be a integer number that is greater or equal to 0.
     */
    int mActionIndex = 0;

    private final static String TAG = "AirSig";

    public ASManager(Context context, CallbackContext callbackContext, String userId) {
        mContext = context;
        mCallbackContext = callbackContext;

        mLicence = mContext.getResources().getString(mContext.getResources().getIdentifier("airsig_licence", "string", mContext.getPackageName()));

        if (userId != null && userId.trim().length()>0 && userId != "null" ) {
            mUserId = userId;
        } else {
            DeviceUuidFactory uuidFactory = new DeviceUuidFactory(context);
            UUID uuid = uuidFactory.getDeviceUuid();
            mUserId = uuid.toString();
        }

        Log.d(TAG,"User id: "+mUserId);
        Log.d(TAG,"Licence: "+mLicence);

        mASGui = new ASGui(mContext.getApplicationContext(), mLicence, mUserId);
        mASEngine = new ASEngine(mContext.getApplicationContext(), mLicence);
        mASEngine.identify(mUserId);

        mASEngine.setOnResultListener(onResultListener);
    }

    public void startRecordSensor() {
        Log.d(TAG,"startRecordSensor");
        mASEngine.startRecordSensor();
    }

    public void completeRecordSensorToIdentifyAction() {
        Log.d(TAG,"completeRecordSensorToIdentifyAction");
        mASEngine.completeRecordSensorToIdentifyAction();
    }

    public void completeRecordSensorToTrainAction() {
        Log.d(TAG,"completeRecordSensorToTrainAction");
        mASEngine.completeRecordSensorToTrainAction(mActionIndex);
    }

    public void deleteRecord()
    {
        Log.d(TAG,"deleteRecord");
        mASEngine.deleteAllActions();
    }

    public void resetSignature()
    {
        Log.d(TAG,"resetSignature");
        mASEngine.resetSignature(mActionIndex);
    }

    // AirSig callback functions
    private ASEngine.OnResultListener onResultListener = new ASEngine.OnResultListener() {
        @Override
        public void onSetActionsResult(ArrayList<ASEngine.ASAction> arg0, ASEngine.Error arg1) {
        }

        @Override
        public void onResetSignatureResult(ASEngine.ASAction arg0, ASEngine.Error arg1) {
        }

        @Override
        public void onIdentifySignatureResult(ASEngine.ASAction arg0, ASEngine.Error arg1) {
        }

        @Override
        public void onHttpError(ASEngine.Error error) {
            mCallbackContext.error("Http error, code: "+error.code+", message: "+error.message+".");
        }

        @Override
        public void onGetAllActionsResult(ArrayList<ASEngine.ASAction> arg0, ASEngine.Error arg1) {
        }

        @Override
        public void onGetActionResult(ASEngine.ASAction action, ASEngine.Error error) {

            if (null != action) {
                Log.d("AirSig", "onGetActionResult: fingerPositionX: " + action.fingerPointX);
                mFingerPositionX = (float) action.fingerPointX;
                mFingerPositionY = (float) action.fingerPointY;

                // if (action.numberOfSignatureStillNeedBeforeVerify > 0)
                // showToast(mContext.getResources().getString(mContext.getResources().getIdentifier("no_signature", "string", mContext.getPackageName())));
                //  else
                //      mReadyToVerify = true;


            } else if (null != error) {
                Log.d(TAG,"onGetActionResult: No signature");
                mCallbackContext.error(mContext.getResources().getString(mContext.getResources().getIdentifier("no_signature", "string", mContext.getPackageName())));
                // showToast(mContext.getResources().getString(mContext.getResources().getIdentifier("no_signature", "string", mContext.getPackageName())));
            }
        }

        @Override
        public void onDeleteAllActionsResult(boolean arg0, ASEngine.Error arg1) {
        }

        @Override
        public void onDeleteActionResult(boolean arg0, ASEngine.Error arg1) {
        }

        @Override
        public void onCompleteRecordSensorToTrainActionResult(ASEngine.ASAction action, ASEngine.Error error) {
            Log.i(TAG, "\nonCompleteRecordSensorToTrainActionResult");
            if (null != action) {
                Log.i(TAG, "action.action: " + action.action);
                Log.i(TAG, "action.actionIndex: " + action.actionIndex);
                Log.i(TAG, "action.fingerPointX: " + action.fingerPointX);
                Log.i(TAG, "action.fingerPointY: " + action.fingerPointY);
                Log.i(TAG, "action.strength: " + action.strength);
                Log.i(TAG, "action.numberOfSignatureStillNeedBeforeVerify: " + action.numberOfSignatureStillNeedBeforeVerify);

                mStrength = action.strength;
                mNumberOfSignatureStillNeed = action.numberOfSignatureStillNeedBeforeVerify;

                if (mStrength == ASEngine.ASStrength.ASStrengthNoData) {
                    mCallbackContext.error(mContext.getString(mContext.getResources().getIdentifier("textview_training_status", "string", mContext.getPackageName()), (mNumberOfSignatureStillNeed), mNumberOfSignatureStillNeed));

                } else {
                    if (action.numberOfSignatureStillNeedBeforeVerify == 0) {


                        // Save data
                        ArrayList<ASEngine.ASAction> actions = new ArrayList<ASEngine.ASAction>();
                        action.fingerPointX = mFingerPositionX;
                        action.fingerPointY = mFingerPositionY;
                        actions.add(action);

                        // Send data to AirSig Server
                        mASEngine.setActions(actions);
                        mCallbackContext.success("Signature recorded.");
                    }
                }
            }

            if (null != error) {
                Log.i(TAG, "error.code: " + error.code);
                Log.i(TAG, "error.message: " + error.message);

                mCallbackContext.error(error.message);
            }
        }

        @Override
        public void onCompleteRecordSensorToIdentifyActionResult(ASEngine.ASAction action, ASEngine.Error error) {
            Log.i(TAG, "\nonCompleteRecordSensorToTrainActionResult");
            boolean pass = false;

            if (null != action) {
                if (ASGui.UNDEFINED == mActionIndex || action.actionIndex == mActionIndex) {
                    Log.i(TAG, "action.numberOfSignatureStillNeedBeforeVerify: " + action.numberOfSignatureStillNeedBeforeVerify);
                    Log.i(TAG, "action.action: " + action.action);
                    Log.i(TAG, "action.actionIndex: " + action.actionIndex);
                    Log.i(TAG, "action.fingerPointX: " + action.fingerPointX);
                    Log.i(TAG, "action.fingerPointY: " + action.fingerPointY);
                    Log.i(TAG, "action.strength: " + action.strength);
                    //showMatchRunnable(true);
                    pass = true;
                    mCallbackContext.success();
                } else {
                    Log.i(TAG, "No match!");
                    mCallbackContext.error("No match!");
                    // showMatchRunnable(false);
                }
            } else if (null != error) {
                Log.i(TAG, "error.code: " + error.code);
                Log.i(TAG, "error.message: " + error.message);
                // showMatchRunnable(false);
                mCallbackContext.error(error.message);
            }
            //showWaitingRunnable(false);
            //startResultTimer(pass);

        }

        @Override
        public void onAddSignaturesResult(ArrayList<ASEngine.ASAction> arg0, ASEngine.Error arg1) {
        }
    };

}
