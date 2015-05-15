package com.airsig.webclientgui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.airsig.webclient.ASEngine;

import java.util.ArrayList;
import java.util.UUID;

//import org.apache.cordova.*;

/**
 * Created by ssoko_000 on 30.3.2015..
 */
public class ASActivityManager {
    Context mContext;
    //CallbackContext mCallbackContext;

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

    private Handler mHandler = new Handler();

    /**
     * mActionIndex is the index number of each air signature. It shall be a integer number that is greater or equal to 0.
     */
    int mActionIndex = 0;

    private String mActivityToShow = "";

    public ASActivityManager(Context context, String userId) {
        mContext = context;
        //mCallbackContext = callbackContext;
        mLicence = mContext.getResources().getString(mContext.getResources().getIdentifier("airsig_licence", "string", mContext.getPackageName()));

        if (userId == null) {
            DeviceUuidFactory uuidFactory = new DeviceUuidFactory(context);
            UUID uuid = uuidFactory.getDeviceUuid();
            mUserId = uuid.toString();
        } else {
            mUserId = userId;
        }

        PrepareEngine();
    }

    private void PrepareEngine() {
        Log.d("AirSig", mUserId);
        mASGui = new ASGui(mContext.getApplicationContext(), mLicence, mUserId);
        mASEngine = new ASEngine(mContext.getApplicationContext(), mLicence);
        mASEngine.identify(mUserId);

        mASEngine.setOnResultListener(new ASEngine.OnResultListener() {

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
            public void onHttpError(ASEngine.Error arg0) {
            }

            @Override
            public void onGetAllActionsResult(ArrayList<ASEngine.ASAction> arg0, ASEngine.Error arg1) {
            }

            @Override
            public void onGetActionResult(ASEngine.ASAction action, ASEngine.Error error) {
                showWaiting(false);
                if (null != action) {
                    Log.d("AirSig", "ASActivity - onGetActionResult - fingerPositionX: " + action.fingerPointX);
                    mFingerPositionX = (float) action.fingerPointX;
                    mFingerPositionY = (float) action.fingerPointY;

                    if (action.numberOfSignatureStillNeedBeforeVerify > 0)
                        showToast(mContext.getResources().getString(mContext.getResources().getIdentifier("no_signature", "string", mContext.getPackageName())));
                    else
                        mReadyToVerify = true;

                    showActivity();
                } else if (null != error) {
                    showToast(mContext.getResources().getString(mContext.getResources().getIdentifier("no_signature", "string", mContext.getPackageName())));
                }
            }

            @Override
            public void onDeleteAllActionsResult(boolean arg0, ASEngine.Error arg1) {
            }

            @Override
            public void onDeleteActionResult(boolean arg0, ASEngine.Error arg1) {
            }

            @Override
            public void onCompleteRecordSensorToTrainActionResult(ASEngine.ASAction arg0, ASEngine.Error arg1) {
            }

            @Override
            public void onCompleteRecordSensorToIdentifyActionResult(ASEngine.ASAction arg0, ASEngine.Error arg1) {
            }

            @Override
            public void onAddSignaturesResult(ArrayList<ASEngine.ASAction> arg0, ASEngine.Error arg1) {
            }
        });
    }

    private void showActivity() {
        if (mActivityToShow.equals("showVerifyActivity")) {
            mASGui.showIdentifyActivity(ASGui.UNDEFINED, mFingerPositionX, mFingerPositionY);
        }
    }

    public Boolean isSignatureValid()
    {
        SharedPreferences prefs = mContext.getSharedPreferences(com.airsig.webclientgui.Const.PREFS_FILE, 0);
        return prefs.getBoolean(Const.PREFS_VALID_SIG, false);
    }

    public boolean resetSignatureValid()
    {
        SharedPreferences prefs = mContext.getSharedPreferences(com.airsig.webclientgui.Const.PREFS_FILE, 0);
        return prefs.edit().putBoolean(Const.PREFS_VALID_SIG, false).commit();
    }

    public void showEntryActivity() {
        Intent intent = new Intent(mContext, EntryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void showTrainingActivity() {
        mASGui.showTrainingActiviy(mActionIndex);
    }

    public void showVerifyActivity() {
        mActivityToShow = "showVerifyActivity";

        mASEngine.getAction(mActionIndex);

        //if (mReadyToVerify)
        // mASGui.showIdentifyActivity(ASGui.UNDEFINED, mFingerPositionX, mFingerPositionY);
        //else
        //     showToast(mContext.getResources().getString(mContext.getResources().getIdentifier("no_signature", "string", mContext.getPackageName())));

//        Intent intent = new Intent(mContext, VerifyActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(intent);
    }

    public void showTutorialActivity() {
        Intent intent = new Intent(mContext, TutorialActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private void showWaiting(final boolean show) {

//        Runnable runnable = new Runnable() {
//
//            @Override
//            public void run() {
//                if (show) {
//                    mProgressBar.setVisibility(View.VISIBLE);
//                    mButtonSetSignature.setEnabled(false);
//                    mButtonVerifySignature.setEnabled(false);
//                    mButtonCleanDb.setEnabled(false);
//                } else {
//                    mProgressBar.setVisibility(View.INVISIBLE);
//                    mButtonSetSignature.setEnabled(true);
//                    mButtonVerifySignature.setEnabled(true);
//                    mButtonCleanDb.setEnabled(true);
//                }
//            }
//        };
//
//        mHandler.post(runnable);
    }

    private void showToast(final String message) {

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        };

        mHandler.post(runnable);
    }
}
