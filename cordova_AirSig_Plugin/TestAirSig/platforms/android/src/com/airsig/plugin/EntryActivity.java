package com.airsig.webclientgui;

import java.lang.String;
import java.util.ArrayList;

import com.airsig.webclient.ASEngine;
import com.airsig.webclient.ASEngine.ASAction;
import com.airsig.webclient.ASEngine.Error;
import com.airsig.webclientgui.ASGui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import android.util.Log;

public class EntryActivity extends ActivityExtension {

    /**
     * Get a valid license key from www.airsig.com.
     */
    private static final String LICENSE = "{A3E09392-17FF-455E-9C46-685B5E303F34}";

    /**
     * mUserId was used to represent the different user. It shall be a specific identification. ex: user's e-mail address.
     */
    String mUserId = "";

    private ASGui mASGui;
    private ASEngine mASEngine;
    private Button mButtonSetSignature;
    private Button mButtonVerifySignature;
    private Button mButtonCleanDb;
    private ProgressBar mProgressBar;
    private boolean mReadyToVerify = false;

    private float mFingerPositionX = ASGui.NOPOSITION;
    private float mFingerPositionY = ASGui.NOPOSITION;

    private Handler mHandler = new Handler();

    /**
     * mActionIndex is the index number of each air signature. It shall be a integer number that is greater or equal to 0.
     */
    int mActionIndex = 0;

    @Override
    protected void onResume() {
        super.onResume();
        mReadyToVerify = false;
        mASEngine.getAction(mActionIndex);
        showWaiting(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((getIdentifier("activity_entry", "layout")));

        mProgressBar = (ProgressBar) findViewById(getIdentifier("progressBar_waiting", "id"));

        mASGui = new ASGui(getApplicationContext(), LICENSE, mUserId);
        mASEngine = new ASEngine(getApplicationContext(), LICENSE);
        mASEngine.identify(mUserId);
        mASEngine.setOnResultListener(new ASEngine.OnResultListener() {

            @Override
            public void onSetActionsResult(ArrayList<ASAction> arg0, Error arg1) {
            }

            @Override
            public void onResetSignatureResult(ASAction arg0, Error arg1) {
            }

            @Override
            public void onIdentifySignatureResult(ASAction arg0, Error arg1) {
            }

            @Override
            public void onHttpError(Error arg0) {
            }

            @Override
            public void onGetAllActionsResult(ArrayList<ASAction> arg0, Error arg1) {
            }

            @Override
            public void onGetActionResult(ASAction action, Error error) {
                showWaiting(false);
                if (null != action) {
                    Log.d("AirSig", "onGetActionResult - fingerPositionX: "+  action.fingerPointX);
                    mFingerPositionX = (float) action.fingerPointX;
                    mFingerPositionY = (float) action.fingerPointY;

                    if (action.numberOfSignatureStillNeedBeforeVerify > 0)
                        showToast(getResources().getString((getIdentifier("no_signature", "string"))));
                    else
                        mReadyToVerify = true;
                } else if (null != error) {
                    showToast(getResources().getString((getIdentifier("no_signature", "string"))));
                }
            }

            @Override
            public void onDeleteAllActionsResult(boolean arg0, Error arg1) {
            }

            @Override
            public void onDeleteActionResult(boolean arg0, Error arg1) {
            }

            @Override
            public void onCompleteRecordSensorToTrainActionResult(ASAction arg0, Error arg1) {
            }

            @Override
            public void onCompleteRecordSensorToIdentifyActionResult(ASAction arg0, Error arg1) {
            }

            @Override
            public void onAddSignaturesResult(ArrayList<ASAction> arg0, Error arg1) {
            }
        });

        mButtonSetSignature = (Button) findViewById(getIdentifier("button_set_signature", "id"));
        mButtonSetSignature.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                mASGui.showTrainingActiviy(mActionIndex);
            }

        });

        mButtonVerifySignature = (Button) findViewById(getIdentifier("button_verify_signature", "id"));
        mButtonVerifySignature.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mReadyToVerify)
                    mASGui.showIdentifyActivity(ASGui.UNDEFINED, mFingerPositionX, mFingerPositionY);
                else
                    showToast(getResources().getString((getIdentifier("no_signature", "string"))));
            }

        });

        mButtonCleanDb = (Button) findViewById(getIdentifier("button_clean_db", "id"));
        mButtonCleanDb.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                mASEngine.deleteAllActions();
                mReadyToVerify = false;
            }

        });
    }

    private void showWaiting(final boolean show) {

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                if (show) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mButtonSetSignature.setEnabled(false);
                    mButtonVerifySignature.setEnabled(false);
                    mButtonCleanDb.setEnabled(false);
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mButtonSetSignature.setEnabled(true);
                    mButtonVerifySignature.setEnabled(true);
                    mButtonCleanDb.setEnabled(true);
                }
            }
        };

        mHandler.post(runnable);
    }

    private void showToast(final String message) {

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        };

        mHandler.post(runnable);
    }
}
