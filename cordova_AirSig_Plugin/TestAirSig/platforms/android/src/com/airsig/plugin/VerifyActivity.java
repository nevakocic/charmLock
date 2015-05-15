package com.airsig.webclientgui;

import java.util.ArrayList;

import com.airsig.webclient.ASEngine;
import com.airsig.webclient.ASEngine.ASAction;
import com.airsig.webclient.ASEngine.Error;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

//import org.apache.cordova.*;

public class VerifyActivity extends ActivityExtension {

    private static final String TAG = "VerifyActivity";
    private ASEngine mASEngine;
    private String mLicense;
    private String mUserId;
    private int mActionIndex;

    private float mFingerPositionX;
    private float mFingerPositionY;

    private ImageButton mButtonClose;
    private ImageView mImageViewTouch;
    private ProgressBar mProgressBar;

    private ImageView mImageViewResult;
    private TextView mTextViewResult;
    private ImageView mImageViewBg;

    private int mTouchViewWidth;
    private int mTouchViewHeight;

    private Handler mHandler = new Handler();

    private SharedPreferences mPrefs;
    //CallbackContext mCallbackContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((getIdentifier("activity_verify", "layout")));

        Bundle bundle = getIntent().getExtras();
        mLicense = bundle.getString(Const.BUNDLE_KEY_LICENSE);
        mUserId = bundle.getString(Const.BUNDLE_KEY_USER_ID);
        mActionIndex = bundle.getInt(Const.BUNDLE_KEY_ACTION_INDEX);
        mFingerPositionX = bundle.getFloat(Const.BUNDLE_KEY_FINGER_POSITION_X);
        mFingerPositionY = bundle.getFloat(Const.BUNDLE_KEY_FINGER_POSITION_Y);

        mPrefs = this.getSharedPreferences(Const.PREFS_FILE, 0);
        mPrefs.edit().putBoolean(Const.PREFS_VALID_SIG,false).commit();

        mProgressBar = (ProgressBar) findViewById(getIdentifier("progressBar_waiting", "id"));

        mButtonClose = (ImageButton) findViewById(getIdentifier("imageButton_close", "id"));
        mImageViewTouch = (ImageView) findViewById(getIdentifier("imageView_touch", "id"));

        mImageViewResult = (ImageView) findViewById(getIdentifier("imageView_result", "id"));
        mTextViewResult = (TextView) findViewById(getIdentifier("textView_result", "id"));
        mImageViewBg = (ImageView) findViewById(getIdentifier("imageView_bg_auth_circle", "id"));

        BitmapDrawable bd = (BitmapDrawable) getDrawable("img_touchposition");
        mTouchViewWidth = bd.getBitmap().getWidth();
        mTouchViewHeight = bd.getBitmap().getHeight();

        mButtonClose.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mImageViewTouch.setOnTouchListener(new ImageButton.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                boolean ret = false;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mImageViewTouch.setColorFilter(Color.argb(255, 255, 255, 255));

                    mASEngine.startRecordSensor();
                    ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(Const.VIBRATOR_DURATION);
                    ret = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mImageViewTouch.setColorFilter(null);

                    mASEngine.completeRecordSensorToIdentifyAction();
                    showTouchArea(false);
                    showWaiting(true);
                    ret = true;
                }
                return ret;
            }
        });

        mASEngine = new ASEngine(getApplicationContext(), mLicense);
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
            public void onCompleteRecordSensorToIdentifyActionResult(ASAction action, Error error) {
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
                        showMatchRunnable(true);
                        pass = true;
                    } else {
                        showMatchRunnable(false);
                    }
                } else if (null != error) {
                    Log.i(TAG, "error.code: " + error.code);
                    Log.i(TAG, "error.message: " + error.message);
                    showMatchRunnable(false);
                }
                showWaitingRunnable(false);
                startResultTimer(pass);
            }

            @Override
            public void onAddSignaturesResult(ArrayList<ASAction> arg0, Error arg1) {

            }
        });

        hideResult();
        showWaiting(false);
        showTouchArea(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startResultTimer(final boolean pass) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    hideResultRunnable();
                    showTouchAreaRunnable(true);
                    if (pass) {
                        mPrefs.edit().putBoolean(Const.PREFS_VALID_SIG,true).commit();
                        //if(mCallbackContext!=null) mCallbackContext.success("Jupi");

                        finish();
                    }
                }
            }
        };
        thread.start();
    }

    private void hideResult() {
        mImageViewResult.setVisibility(View.INVISIBLE);
        mTextViewResult.setVisibility(View.INVISIBLE);
        mImageViewBg.setImageDrawable(getDrawable("bg_authenticate_circle"));
    }

    private void hideResultRunnable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                hideResult();
            }
        };
        mHandler.post(runnable);
    }

    private void showTouchArea(boolean show) {
        if (show) {
            if (ASGui.NOPOSITION != mFingerPositionX && ASGui.NOPOSITION != mFingerPositionY) {
                mImageViewTouch.setVisibility(View.VISIBLE);
                mImageViewTouch.setX((int) mFingerPositionX - mTouchViewWidth / 2);
                mImageViewTouch.setY((int) mFingerPositionY - mTouchViewHeight / 2);
            }
        } else {
            mImageViewTouch.setVisibility(View.INVISIBLE);
        }
    }

    private void showTouchAreaRunnable(final boolean show) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                showTouchArea(show);
            }
        };
        mHandler.post(runnable);
    }

    private void showMatch(boolean match) {
        mImageViewResult.setVisibility(View.VISIBLE);
        mTextViewResult.setVisibility(View.VISIBLE);
        if (match) {
            mImageViewBg.setImageDrawable(getDrawable("bg_authenticate_circle_success"));
            mImageViewResult.setImageDrawable(getDrawable("ic_done_big_white" ));
            mTextViewResult.setText((getIdentifier("verify_match", "string")));
        } else {
            mImageViewBg.setImageDrawable(getDrawable("bg_authenticate_circle_fail"));
            mImageViewResult.setImageDrawable(getDrawable("ic_fail_big_white"));
            mTextViewResult.setText((getIdentifier("verify_not_match", "string")));
        }
    }

    private void showMatchRunnable(final boolean match) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                showMatch(match);
            }
        };
        mHandler.post(runnable);
    }

    private void showWaiting(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showWaitingRunnable(final boolean show) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                showWaiting(show);
            }
        };
        mHandler.post(runnable);
    }
}
