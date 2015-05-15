package com.airsig.webclientgui;

import com.airsig.webclient.ASEngine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ASGui {

    public static final int UNDEFINED = ASEngine.UNDEFINED;
    public static final float NOPOSITION = ASEngine.NOPOSITION;

    Context mContext;
    String mLicense;
    String mUserId;

    public ASGui(Context context, String license, String userId) {

        mContext = context;
        mLicense = license;
        mUserId = userId;
    }

    public void showTrainingActiviy(int actionIndex) {

        Intent intent = new Intent();
        intent.setClass(mContext, TrainingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString(Const.BUNDLE_KEY_LICENSE, mLicense);
        bundle.putString(Const.BUNDLE_KEY_USER_ID, mUserId);
        bundle.putInt(Const.BUNDLE_KEY_ACTION_INDEX, actionIndex);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    public void showIdentifyActivity(int actionIndex, float fingerPositionX, float fingerPositionY) {
        Log.d("AirSig", "showIdentifyActivity - fingerPositionX: "+ fingerPositionX);
        Intent intent = new Intent();
        intent.setClass(mContext, VerifyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
        bundle.putString(Const.BUNDLE_KEY_LICENSE, mLicense);
        bundle.putString(Const.BUNDLE_KEY_USER_ID, mUserId);
        bundle.putInt(Const.BUNDLE_KEY_ACTION_INDEX, actionIndex);
        bundle.putFloat(Const.BUNDLE_KEY_FINGER_POSITION_X, fingerPositionX);
        bundle.putFloat(Const.BUNDLE_KEY_FINGER_POSITION_Y, fingerPositionY);
        intent.putExtras(bundle);

        mContext.startActivity(intent);
    }
}
