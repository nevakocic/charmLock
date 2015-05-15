package com.airsig.webclientgui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airsig.webclient.ASEngine;
import com.airsig.webclient.ASEngine.ASAction;
import com.airsig.webclient.ASEngine.ASStrength;
import com.airsig.webclient.ASEngine.Error;
import com.airsig.webclient.ASEngine.OnResultListener;

public class TrainingActivity extends ActivityExtension {

    private final static String TAG = "AirSig";

    // Parameters for counting training times
    private final static int MIN_TRAINING_TIMES = 3;
    private final static int MAX_TRAINING_TIMES = 5;

    // Parameters for SharedPreferences
    private SharedPreferences mSharedPreferences;
    private static final String SP_DATA_FILE = "AIRSIG_DATA";
    private static final String SP_FIELD_SUB_X = "SUB_POSITION_X";
    private static final String SP_FIELD_SUB_Y = "SUB_POSITION_Y";

    // Parameters for AirSig Engine
    private int mActionIndex = 0;
    private int mNumberOfSignatureStillNeed = MAX_TRAINING_TIMES;
    private float mFingerPointX = 0;
    private float mFingerPointY = 0;
    private float mFingerSubPointX = 0;
    private float mFingerSubPointY = 0;
    private boolean mResetFlag = false;
    private ASEngine mASEngine = null;
    private ASStrength mStrength = ASEngine.ASStrength.ASStrengthNoData;


    /* ------------------------------------ UI parameters ------------------------------------ */
    private int mScreen = 1;

    private TextView mTextView_Title;                                        // For 1st to 5th screen
    private TextView mTextView_Description;                                    // For 1st to 5th screen
    private ImageView mImg_Finger;                                            // For 1st screen
    private ImageView mImgView_Background_screen1_and_screen2;                // For 1st and 2nd screen

    private ImageView mImg_ThumbPosition;                                    // For 2nd screen

    private ImageView mImgView_Background_screen3_to_screen6;                // For 3rd to 6th screen
    private LinearLayout mLayout_SetThumb;                                    // For 3rd screen
    private ImageView mImgView_SetThumbImg;                                    // For 3rd screen

    private TextView mTextView_SetThumbText;                                // For 3rd screen
    private Button mBtn_NextStep;                                            // For 3rd and 6th screen

    private LinearLayout mLayout_RecordingLayout;                            // For 5th screen

    private TextView mTextView_TrainingStatus;                                // For 6th screen

    private ImageView mImgView_Background_screen7;                            // For 7th screen
    private LinearLayout mLayout_TrainingFinishLayout;                        // For 7th screen
    private TextView mTextView_TrainingResult;                                // For 7th screen
    private Button mBtn_SignAgain;                                            // For 7th screen
    private Button mBtn_TestSign;                                            // For 7th screen

    private RelativeLayout mLayout_progressbar;                                // For 1st and 5th screen

    private ImageButton mImgBtn_Help;                                        // For all screen
    private RelativeLayout mlayout_whole_background;                        // For all screen

    /* ------------------------------------ Check double click time interval ----------------- */
    private long mActionDownTime = 0;
    private long mClickTime = 0;
    private final static long DOUBLE_CLICK_TIME_INTERVAL = 250;                // millisecond
    /* --------------------------------------------------------------------------------------- */

    private String mLicense;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getIdentifier("training_activity", "layout"));

        // Get bundle parameters
        Bundle bundle = this.getIntent().getExtras();
        if (bundle==null) {
            Log.e("AirSig","TrainingActivity extras null.");
        }
Log.i("AirSig","pre set");
        mActionIndex = bundle.getInt(Const.BUNDLE_KEY_ACTION_INDEX);
        mLicense = bundle.getString(Const.BUNDLE_KEY_LICENSE);
        mUserId = bundle.getString(Const.BUNDLE_KEY_USER_ID);

        // Create an instance of AirSig engine
        mASEngine = new ASEngine(getApplicationContext(), mLicense);
        if (mASEngine==null) {
            Log.e("AirSig","mASEngine is null.");
        }
        // Set user's information
        mASEngine.identify(mUserId);

        // Set AirSig listener
        mASEngine.setOnResultListener(mASEngneLR);
		
		/* ------------------------ Get UI layout ---------------------------------- */
        mTextView_Title = (TextView) findViewById(getIdentifier("text_title", "id"));
        mTextView_SetThumbText = (TextView) findViewById(getIdentifier("text_set_thumb", "id"));
        mTextView_Description = (TextView) findViewById(getIdentifier("text_description", "id"));
        mTextView_TrainingResult = (TextView) findViewById(getIdentifier("text_training_result", "id"));
        mTextView_TrainingStatus = (TextView) findViewById(getIdentifier("text_training_status", "id"));

        mImg_Finger = (ImageView) findViewById(getIdentifier("img_finger", "id"));
        mImgView_SetThumbImg = (ImageView) findViewById(getIdentifier("img_set_thumb", "id"));
        mImg_ThumbPosition = (ImageView) findViewById(getIdentifier("img_thumbPosition", "id"));
        mImgView_Background_screen7 = (ImageView) findViewById(getIdentifier("img_training_finish_background", "id"));
        mImgView_Background_screen3_to_screen6 = (ImageView) findViewById(getIdentifier("img_set_signature_background", "id"));
        mImgView_Background_screen1_and_screen2 = (ImageView) findViewById(getIdentifier("img_set_thumb_position_background", "id"));

        mLayout_SetThumb = (LinearLayout) findViewById(getIdentifier("llayout_set_thumb", "id"));
        mLayout_RecordingLayout = (LinearLayout) findViewById(getIdentifier("llayout_recording", "id"));
        mLayout_TrainingFinishLayout = (LinearLayout) findViewById(getIdentifier("llayout_training_finish", "id"));
        mlayout_whole_background = (RelativeLayout) findViewById(getIdentifier("rlayout_whole_background", "id"));

        mBtn_NextStep = (Button) findViewById(getIdentifier("btn_nextStep", "id"));
        mBtn_SignAgain = (Button) findViewById(getIdentifier("btn_sign_again", "id"));
        mBtn_TestSign = (Button) findViewById(getIdentifier("btn_test_signature", "id"));

        mImgBtn_Help = (ImageButton) findViewById(getIdentifier("imgbtn_helpBtn", "id"));

        mLayout_progressbar = (RelativeLayout) findViewById(getIdentifier("rlayout_progressbar", "id"));
		/* ------------------------------------------------------------------------ */

        // Get previous setting from server
        mLayout_progressbar.setVisibility(View.VISIBLE);

        // Get previous setting from server
        mASEngine.getAction(mActionIndex);

        //
        mImgBtn_Help.setOnClickListener(mhelpBtnLR);

    }

    protected void onPause() {
        super.onPause();

        // Save data if user does not finish training
        if (mStrength == ASEngine.ASStrength.ASStrengthNoData) {
            if (mNumberOfSignatureStillNeed < MIN_TRAINING_TIMES) {
                // Show waiting cursor
                mLayout_progressbar.setVisibility(View.VISIBLE);

                // Save data
                ArrayList<ASAction> actions = new ArrayList<ASAction>();
                ASAction aAction = null;

                aAction = new ASAction();
                aAction.actionIndex = mActionIndex;
                aAction.fingerPointX = mFingerPointX;
                aAction.fingerPointY = mFingerPointY;
                actions.add(aAction);

                // Send data to AirSig Server
                mASEngine.setActions(actions);
            }
        }
    }

    // "Help" button
    private OnClickListener mhelpBtnLR = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), TutorialActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            getApplicationContext().startActivity(intent);
        }
    };

    // Button for set coordinates of thumb on screen
    private OnTouchListener mSetThumbPositionLR = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mImg_ThumbPosition.setImageResource(getIdentifier("ani_doubleclick_03", "drawable"));
                    if (mScreen == 1) {
                        mScreen = 2;
                        setScreen(mScreen);
                    }

                    mActionDownTime = event.getEventTime();
                case MotionEvent.ACTION_MOVE:
                    // Move icon
                    mImg_ThumbPosition.setX(event.getX() - mImg_ThumbPosition.getWidth() / 2);
                    mImg_ThumbPosition.setY(event.getY() - mImg_ThumbPosition.getHeight() / 2);
                    break;
                case MotionEvent.ACTION_UP:
                    mImg_ThumbPosition.setImageResource(getIdentifier("ani_doubleclick_01", "drawable"));
                    if (event.getEventTime() - mActionDownTime < DOUBLE_CLICK_TIME_INTERVAL) { // is click? (millisecond)
                        if (event.getEventTime() - mClickTime < DOUBLE_CLICK_TIME_INTERVAL) { // is double click? (millisecond)

                            if (mScreen == 2) {
							/* --------------------------------------------------------------------------------
							 * Save to DB: getRawX() and getRawY()
							 * Show on screen:
							 * 		x: mImg_ThumbPosition.setX(event.getX() - mImg_ThumbPosition.getWidth()/2);	
							 * 		y: mImg_ThumbPosition.setY(event.getY() - mImg_ThumbPosition.getHeight()/2);
							 * --------------------------------------------------------------------------------
							 */

                                // Switch to 3rd screen
                                mScreen = 3;
                                setScreen(mScreen);

                                // Lock screen after double click
                                mlayout_whole_background.setOnTouchListener(null);

                                // Coordinates of thumb on screen
                                mFingerPointX = event.getRawX();
                                mFingerPointY = event.getRawY();

                                // Save SharedPreferences
                                savePreferences(event.getRawX() - event.getX(), event.getRawY() - event.getY());

                                // Save coordinates of thumb to server in background
                                ArrayList<ASAction> actions = new ArrayList<ASAction>();
                                ASAction aAction = null;

                                aAction = new ASAction();
                                aAction.actionIndex = mActionIndex;
                                aAction.fingerPointX = mFingerPointX;
                                aAction.fingerPointY = mFingerPointY;
                                actions.add(aAction);

                                // Send data to AirSig Server
                                mASEngine.setActions(actions);

                                // Next step button listener
                                mBtn_NextStep.setOnClickListener(mNextStepLR);
                            }
                        }
                        mClickTime = event.getEventTime();

                    }
                    break;
            }
            return true;
        }
    };

    // "Next Step" button
    private OnClickListener mNextStepLR = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (mScreen == 3) {
                // Switch to 4th screen
                mScreen = 4;
                setScreen(mScreen);
            }

            if (mScreen == 6) {

                if (mStrength != ASEngine.ASStrength.ASStrengthNoData && mNumberOfSignatureStillNeed == 0) {
                    // Switch to 7th screen if got training result from server
                    mScreen = 7;
                    setScreen(mScreen);
                } else {
                    // Switch to 4th screen if still need training data
                    mScreen = 4;
                    setScreen(mScreen);
                }
            }
        }
    };

    // Record signature
    private OnTouchListener mRecordSignatureLR = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mScreen == 4) {
                        // Switch to 5th screen
                        mScreen = 5;
                        setScreen(mScreen);

                        // Let user sign 5 signatures, first two signatures are for practice, and only send last 3 signatures to server.
                        if (mNumberOfSignatureStillNeed <= MIN_TRAINING_TIMES) {
                            mASEngine.startRecordSensor();
                        }

                        Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        vb.vibrate(Const.VIBRATOR_DURATION);
                    }

                    break;

                case MotionEvent.ACTION_UP:

                    // Let user sign 5 signatures, first two signatures are for practice, and only send last 3 signatures to server.
                    if (mNumberOfSignatureStillNeed <= MIN_TRAINING_TIMES) {
                        // Disable recording icon
                        mLayout_RecordingLayout.setVisibility(View.GONE);

                        // Show wait cursor when send signature to server
                        mLayout_progressbar.setVisibility(View.VISIBLE);
                        //mProgressBar_waiting.setVisibility(View.VISIBLE);
                        mImg_ThumbPosition.setVisibility(View.GONE);

                        // Send signature to server and switch to 6th screen
                        mASEngine.completeRecordSensorToTrainAction(mActionIndex);
                    } else {
                        mNumberOfSignatureStillNeed--;
                        // First two signs are for practices, so switch to 6th screen without sending data to server
                        if (mScreen == 5) {

                            // Switch to 6th screen
                            mScreen = 6;
                            setScreen(mScreen);

                            // Set how many training times are still needed
                            mTextView_TrainingStatus.setText(getString((getIdentifier("textview_training_status", "string")), (MAX_TRAINING_TIMES - mNumberOfSignatureStillNeed), mNumberOfSignatureStillNeed));
                        }
                    }
                    break;
            }
            return true;
        }
    };

    // "Sign Again" button
    private OnClickListener mSignAgainLR = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            if (mScreen == 7) {
                // Switch to 4th screen
                mScreen = 4;
                setScreen(mScreen);
            }
        }
    };

    private OnClickListener mTestSignatureLR = new OnClickListener() {

        @Override
        public void onClick(View arg0) {

            ASGui asgui = new ASGui(getApplicationContext(), mLicense, mUserId);
            asgui.showIdentifyActivity(mActionIndex, mFingerPointX, mFingerPointY);
        }
    };

    // Menu of actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate((getIdentifier("actionbar_training_mode", "menu")), menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Actionbar functions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        if (getIdentifier("reset", "id") == item.getItemId()) {

            // Show waiting cursor
            mLayout_progressbar.setVisibility(View.VISIBLE);
            //mProgressBar_waiting.setVisibility(View.VISIBLE);
            mASEngine.resetSignature(mActionIndex);

            // Set as default;
            mFingerPointX = 0;
            mFingerPointY = 0;
            mNumberOfSignatureStillNeed = MAX_TRAINING_TIMES;

            mResetFlag = true;

        } else if (getIdentifier("done", "id") == item.getItemId()) {

            if (mStrength != ASEngine.ASStrength.ASStrengthNoData) {

                // Show waiting cursor
                mLayout_progressbar.setVisibility(View.VISIBLE);

                // Save data
                ArrayList<ASAction> actions = new ArrayList<ASAction>();
                ASAction aAction = null;

                aAction = new ASAction();
                aAction.actionIndex = mActionIndex;
                aAction.fingerPointX = mFingerPointX;
                aAction.fingerPointY = mFingerPointY;
                actions.add(aAction);

                // Send data to AirSig Server
                mASEngine.setActions(actions);

                // Finish training activity
                this.finish();
            } else {
                openPopupMsg(getString((getIdentifier("training_popup_not_finish_training", "string"))));
            }
        }

        return true;
    }

    // Switch screen (screen1 ~ screen7)
    private void setScreen(int screenNo) {

        switch (screenNo) {
            case 1:
                // Enable view
                mTextView_Title.setVisibility(View.VISIBLE);                            // For 1st to 5th screen
                mTextView_Description.setVisibility(View.VISIBLE);                        // For 1st to 5th screen
                mImg_Finger.setVisibility(View.VISIBLE);                                // For 1st screen
                mImgView_Background_screen1_and_screen2.setVisibility(View.VISIBLE);    // For 1st and 2nd screen

                mImgBtn_Help.setVisibility(View.VISIBLE);                                // For all screen
                mlayout_whole_background.setVisibility(View.VISIBLE);                    // For all screen


                // Disable view
                mImg_ThumbPosition.setVisibility(View.GONE);                            // For 2nd to 5th screen

                mImgView_Background_screen3_to_screen6.setVisibility(View.GONE);        // For 3rd to 6th screen
                mLayout_SetThumb.setVisibility(View.GONE);                                // For 3rd screen
                mImgView_SetThumbImg.setVisibility(View.GONE);                            // For 3rd screen

                mTextView_SetThumbText.setVisibility(View.GONE);                        // For 3rd screen
                mBtn_NextStep.setVisibility(View.GONE);                                    // For 3rd and 6th screen

                mLayout_RecordingLayout.setVisibility(View.GONE);                        // For 5th screen

                mTextView_TrainingStatus.setVisibility(View.GONE);                        // For 6th screen

                mImgView_Background_screen7.setVisibility(View.GONE);                    // For 7th screen
                mLayout_TrainingFinishLayout.setVisibility(View.GONE);                    // For 7th screen
                mTextView_TrainingResult.setVisibility(View.GONE);                        // For 7th screen
                mBtn_SignAgain.setVisibility(View.GONE);                                // For 7th screen
                mBtn_TestSign.setVisibility(View.GONE);                                    // For 7th screen

                // Set title and description
                mTextView_Title.setText((getIdentifier("textview_title_screen1_3", "string")));
                mTextView_Description.setText((getIdentifier("textview_description_screen1_3", "string")));

                // Unregister signature recording button
                mImg_ThumbPosition.setOnTouchListener(null);

                // Enable set coordinates of thumb
                mlayout_whole_background.setOnTouchListener(mSetThumbPositionLR);


                break;

            case 2:

                // Enable view
                mTextView_Title.setVisibility(View.VISIBLE);                            // For 1st to 5th screen
                mTextView_Description.setVisibility(View.VISIBLE);                        // For 1st to 5th screen
                mImgView_Background_screen1_and_screen2.setVisibility(View.VISIBLE);    // For 1st and 2nd screen
                mImg_ThumbPosition.setVisibility(View.VISIBLE);                            // For 2nd to 5th screen
                mImgBtn_Help.setVisibility(View.VISIBLE);                                // For all screen

                // Disable view
                mImg_Finger.setVisibility(View.GONE);                                    // For 1st screen

                mImgView_Background_screen3_to_screen6.setVisibility(View.GONE);        // For 3rd to 6th screen
                mLayout_SetThumb.setVisibility(View.GONE);                                // For 3rd screen
                mImgView_SetThumbImg.setVisibility(View.GONE);                            // For 3rd screen

                mTextView_SetThumbText.setVisibility(View.GONE);                        // For 3rd screen
                mBtn_NextStep.setVisibility(View.GONE);                                    // For 3rd and 6th screen

                mLayout_RecordingLayout.setVisibility(View.GONE);                        // For 5th screen

                mTextView_TrainingStatus.setVisibility(View.GONE);                        // For 6th screen

                mImgView_Background_screen7.setVisibility(View.GONE);                    // For 7th screen
                mLayout_TrainingFinishLayout.setVisibility(View.GONE);                    // For 7th screen
                mTextView_TrainingResult.setVisibility(View.GONE);                        // For 7th screen
                mBtn_SignAgain.setVisibility(View.GONE);                                // For 7th screen
                mBtn_TestSign.setVisibility(View.GONE);                                    // For 7th screen

                // Set title and description
                mTextView_Title.setText((getIdentifier("textview_title_screen1_3", "string")));
                mTextView_Description.setText((getIdentifier("textview_description_screen1_3", "string")));

                break;

            case 3:
                // Enable view
                mTextView_Title.setVisibility(View.VISIBLE);                            // For 1st to 5th screen
                mTextView_Description.setVisibility(View.VISIBLE);                        // For 1st to 5th screen
                mImg_ThumbPosition.setVisibility(View.VISIBLE);                            // For 2nd to 5th screen
                mImgView_Background_screen3_to_screen6.setVisibility(View.VISIBLE);        // For 3rd to 6th screen
                mLayout_SetThumb.setVisibility(View.VISIBLE);                            // For 3rd screen
                mImgView_SetThumbImg.setVisibility(View.VISIBLE);                        // For 3rd screen
                mTextView_SetThumbText.setVisibility(View.VISIBLE);                        // For 3rd screen
                mBtn_NextStep.setVisibility(View.VISIBLE);                                // For 3rd and 6th screen
                mImgBtn_Help.setVisibility(View.VISIBLE);                                // For all screen

                // Disable view
                mImg_Finger.setVisibility(View.GONE);                                    // For 1st screen
                mImgView_Background_screen1_and_screen2.setVisibility(View.GONE);        // For 1st and 2nd screen
                mLayout_progressbar.setVisibility(View.GONE);                            // For 1s and 5th screen

                mLayout_RecordingLayout.setVisibility(View.GONE);                        // For 5th screen

                mTextView_TrainingStatus.setVisibility(View.GONE);                        // For 6th screen

                mImgView_Background_screen7.setVisibility(View.GONE);                    // For 7th screen
                mLayout_TrainingFinishLayout.setVisibility(View.GONE);                    // For 7th screen
                mTextView_TrainingResult.setVisibility(View.GONE);                        // For 7th screen
                mBtn_SignAgain.setVisibility(View.GONE);                                // For 7th screen
                mBtn_TestSign.setVisibility(View.GONE);                                    // For 7th screen

                // Set title and description
                mTextView_Title.setText((getIdentifier("textview_title_screen1_3", "string")));
                mTextView_Description.setText((getIdentifier("textview_description_screen1_3", "string")));
                break;

            case 4:

                // Enable view
                mTextView_Title.setVisibility(View.VISIBLE);                            // For 1st to 5th screen
                mTextView_Description.setVisibility(View.VISIBLE);                        // For 1st to 5th screen
                mImg_ThumbPosition.setVisibility(View.VISIBLE);                            // For 2nd to 5th screen
                mImgView_Background_screen3_to_screen6.setVisibility(View.VISIBLE);        // For 3rd to 6th screen
                mImgBtn_Help.setVisibility(View.VISIBLE);                                // For all screen

                // Disable view
                mImg_Finger.setVisibility(View.GONE);                                    // For 1st screen
                mImgView_Background_screen1_and_screen2.setVisibility(View.GONE);        // For 1st and 2nd screen

                mLayout_SetThumb.setVisibility(View.GONE);                                // For 3rd screen
                mImgView_SetThumbImg.setVisibility(View.GONE);                            // For 3rd screen

                mTextView_SetThumbText.setVisibility(View.GONE);                        // For 3rd screen
                mBtn_NextStep.setVisibility(View.GONE);                                    // For 3rd and 6th screen

                mLayout_RecordingLayout.setVisibility(View.GONE);                        // For 5th screen

                mTextView_TrainingStatus.setVisibility(View.GONE);                        // For 6th screen

                mImgView_Background_screen7.setVisibility(View.GONE);                    // For 7th screen
                mLayout_TrainingFinishLayout.setVisibility(View.GONE);                    // For 7th screen
                mTextView_TrainingResult.setVisibility(View.GONE);                        // For 7th screen
                mBtn_SignAgain.setVisibility(View.GONE);                                // For 7th screen
                mBtn_TestSign.setVisibility(View.GONE);                                    // For 7th screen

                // Set parameter values
                mTextView_Title.setText((getIdentifier("textview_title_screen4_5", "string")));
                mTextView_Description.setText((getIdentifier("textview_description_screen4_5", "string")));

                // Record signature
                mImg_ThumbPosition.setOnTouchListener(mRecordSignatureLR);

                // Get subX and subY from SharedPreferences, and set previous position
                getPreferences();
                mImg_ThumbPosition.setX((mFingerPointX - mFingerSubPointX) - mImg_ThumbPosition.getWidth() / 2);
                mImg_ThumbPosition.setY((mFingerPointY - mFingerSubPointY) - mImg_ThumbPosition.getHeight() / 2);

                break;

            case 5:
                // Enable view
                mTextView_Title.setVisibility(View.VISIBLE);                            // For 1st to 5th screen
                mTextView_Description.setVisibility(View.VISIBLE);                        // For 1st to 5th screen
                mImg_ThumbPosition.setVisibility(View.VISIBLE);                            // For 2nd to 5th screen
                mImgView_Background_screen3_to_screen6.setVisibility(View.VISIBLE);        // For 3rd to 6th screen
                mLayout_RecordingLayout.setVisibility(View.VISIBLE);                    // For 5th screen
                mImgBtn_Help.setVisibility(View.VISIBLE);                                // For all screen

                // Disable view
                mImg_Finger.setVisibility(View.GONE);                                    // For 1st screen
                mImgView_Background_screen1_and_screen2.setVisibility(View.GONE);        // For 1st and 2nd screen

                mLayout_SetThumb.setVisibility(View.GONE);                                // For 3rd screen
                mImgView_SetThumbImg.setVisibility(View.GONE);                            // For 3rd screen

                mTextView_SetThumbText.setVisibility(View.GONE);                        // For 3rd screen
                mBtn_NextStep.setVisibility(View.GONE);                                    // For 3rd and 6th screen

                mTextView_TrainingStatus.setVisibility(View.GONE);                        // For 6th screen

                mImgView_Background_screen7.setVisibility(View.GONE);                    // For 7th screen
                mLayout_TrainingFinishLayout.setVisibility(View.GONE);                    // For 7th screen
                mTextView_TrainingResult.setVisibility(View.GONE);                        // For 7th screen
                mBtn_SignAgain.setVisibility(View.GONE);                                // For 7th screen
                mBtn_TestSign.setVisibility(View.GONE);                                    // For 7th screen

                mTextView_Title.setText((getIdentifier("textview_title_screen4_5", "string")));
                mTextView_Description.setText((getIdentifier("textview_description_screen4_5", "string")));

                break;
            case 6:
                // Enable view
                mImgView_Background_screen3_to_screen6.setVisibility(View.VISIBLE);        // For 3rd to 6th screen
                mBtn_NextStep.setVisibility(View.VISIBLE);                                // For 3rd and 6th screen
                mTextView_TrainingStatus.setVisibility(View.VISIBLE);                    // For 6th screen

                mImgBtn_Help.setVisibility(View.VISIBLE);                                // For all screen

                // Disable view
                mTextView_Title.setVisibility(View.GONE);                                // For 1st to 5th screen
                mTextView_Description.setVisibility(View.GONE);                            // For 1st to 5th screen
                mImg_Finger.setVisibility(View.GONE);                                    // For 1st screen
                mImgView_Background_screen1_and_screen2.setVisibility(View.GONE);        // For 1st and 2nd screen

                mImg_ThumbPosition.setVisibility(View.GONE);                            // For 2nd to 5th screen

                mLayout_SetThumb.setVisibility(View.GONE);                                // For 3rd screen
                mImgView_SetThumbImg.setVisibility(View.GONE);                            // For 3rd screen
                mTextView_SetThumbText.setVisibility(View.GONE);                        // For 3rd screen

                mLayout_RecordingLayout.setVisibility(View.GONE);                        // For 5th screen

                mImgView_Background_screen7.setVisibility(View.GONE);                    // For 7th screen
                mLayout_TrainingFinishLayout.setVisibility(View.GONE);                    // For 7th screen
                mTextView_TrainingResult.setVisibility(View.GONE);                        // For 7th screen
                mBtn_SignAgain.setVisibility(View.GONE);                                // For 7th screen
                mBtn_TestSign.setVisibility(View.GONE);                                    // For 7th screen

                // Nest step button listener
                mBtn_NextStep.setOnClickListener(mNextStepLR);

                break;
            case 7:
                // Enable view
                mImgView_Background_screen7.setVisibility(View.VISIBLE);                // For 7th screen
                mLayout_TrainingFinishLayout.setVisibility(View.VISIBLE);                // For 7th screen
                mBtn_SignAgain.setVisibility(View.VISIBLE);                                // For 7th screen
                mBtn_TestSign.setVisibility(View.VISIBLE);                                // For 7th screen
                mTextView_TrainingResult.setVisibility(View.VISIBLE);                    // For 7th screen
                mImgBtn_Help.setVisibility(View.VISIBLE);                                // For all screen

                // Disable view

                mTextView_Title.setVisibility(View.GONE);                                // For 1st to 5th screen
                mTextView_Description.setVisibility(View.GONE);                            // For 1st to 5th screen
                mImg_Finger.setVisibility(View.GONE);                                    // For 1st screen
                mImgView_Background_screen1_and_screen2.setVisibility(View.GONE);        // For 1st and 2nd screen

                mImg_ThumbPosition.setVisibility(View.GONE);                            // For 2nd to 5th screen

                mImgView_Background_screen3_to_screen6.setVisibility(View.GONE);        // For 3rd to 6th screen
                mLayout_SetThumb.setVisibility(View.GONE);                                // For 3rd screen
                mImgView_SetThumbImg.setVisibility(View.GONE);                            // For 3rd screen

                mTextView_SetThumbText.setVisibility(View.GONE);                        // For 3rd screen
                mBtn_NextStep.setVisibility(View.GONE);                                    // For 3rd and 6th screen

                mLayout_RecordingLayout.setVisibility(View.GONE);                        // For 5th screen

                mTextView_TrainingStatus.setVisibility(View.GONE);                        // For 6th screen


                // Sign signature again
                mBtn_SignAgain.setOnClickListener(mSignAgainLR);

                // Test Signature
                mBtn_TestSign.setOnClickListener(mTestSignatureLR);

                // Show training result on UI
                showTrainingResultToUI(mStrength);

                break;
            default:
                TrainingActivity.this.finish();
                break;

        }
    }

    // AirSig callback functions
    private OnResultListener mASEngneLR = new ASEngine.OnResultListener() {

        @Override
        public void onGetActionResult(final ASAction action, final Error error) {

            Log.i(TAG, "\nonGetActionResult");
            if (null != action) {
                Log.i(TAG, "action.action: " + action.action);
                Log.i(TAG, "action.actionIndex: " + action.actionIndex);
                Log.i(TAG, "action.fingerPointX: " + action.fingerPointX);
                Log.i(TAG, "action.fingerPointY: " + action.fingerPointY);
                Log.i(TAG, "action.strength: " + action.strength.getInt());
                Log.i(TAG, "action.numberOfSignatureStillNeedBeforeVerify: " + action.numberOfSignatureStillNeedBeforeVerify);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {

                        // Disable waiting cursor after get data from server
                        mLayout_progressbar.setVisibility(View.GONE);

                        // If there is no signature in server, then ask user to sign new signature
                        if (action.numberOfSignatureStillNeedBeforeVerify > 0) {

                            // Switch to 1st screen
                            mScreen = 1;
                            setScreen(mScreen);
                        }

                        // If there is a signature in server, then show finished screen
                        if (action.strength != ASEngine.ASStrength.ASStrengthNoData && action.numberOfSignatureStillNeedBeforeVerify == 0) {

                            mStrength = action.strength;
                            mFingerPointX = (float) action.fingerPointX;
                            mFingerPointY = (float) action.fingerPointY;
                            mNumberOfSignatureStillNeed = action.numberOfSignatureStillNeedBeforeVerify;

                            // Switch to 7th screen
                            mScreen = 7;
                            setScreen(mScreen);
                        }

                        // If previous setting is not finished, then continue to sign previous signature
                        if ((int) action.fingerPointX > 0 && (int) action.fingerPointY > 0 && action.strength == ASEngine.ASStrength.ASStrengthNoData) {

                            mStrength = action.strength;
                            mFingerPointX = (float) action.fingerPointX;
                            mFingerPointY = (float) action.fingerPointY;
                            mNumberOfSignatureStillNeed = action.numberOfSignatureStillNeedBeforeVerify;

                            // Switch to 4th screen
                            mScreen = 4;
                            setScreen(mScreen);

                        }
                    }
                });
            }

            if (null != error) {
                Log.i(TAG, "error.code: " + error.code);
                Log.i(TAG, "error.message: " + error.message);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        // Disable waiting cursor
                        mLayout_progressbar.setVisibility(View.GONE);


                        if (error.code == -1) {
                            mScreen = 1;
                            setScreen(mScreen);
                        } else {
                            // Popup error message
                            openPopupMsg(getPopupString(error.code));

                        }
                    }
                });
            }
        }

        @Override
        public void onGetAllActionsResult(ArrayList<ASAction> actions, Error error) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSetActionsResult(ArrayList<ASAction> actions, final Error error) {

            Log.i(TAG, "\nonSetActionsResult");
            if (null != actions) {
                for (int i = 0; i < actions.size(); i++) {
                    Log.i(TAG, "actions.get(" + i + ").action: " + actions.get(i).action);
                    Log.i(TAG, "actions.get(" + i + ").actionIndex: " + actions.get(i).actionIndex);
                    Log.i(TAG, "actions.get(" + i + ").fingerPointX: " + actions.get(i).fingerPointX);
                    Log.i(TAG, "actions.get(" + i + ").fingerPointY: " + actions.get(i).fingerPointY);
                    Log.i(TAG, "actions.get(" + i + ").strength: " + actions.get(i).strength);
                    Log.i(TAG, "actions.get(" + i + ").numberOfSignatureStillNeedBeforeVerify: " + actions.get(i).numberOfSignatureStillNeedBeforeVerify);
                }

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        // Disable waiting cursor after get data from server
                        mLayout_progressbar.setVisibility(View.GONE);
                    }
                });
            }

            if (null != error) {
                Log.i(TAG, "error.code: " + error.code);
                Log.i(TAG, "error.message: " + error.message);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        // Disable waiting cursor
                        mLayout_progressbar.setVisibility(View.GONE);
                        // Popup error message
                        openPopupMsg(getPopupString(error.code));
                    }
                });
            }
        }

        @Override
        public void onAddSignaturesResult(ArrayList<ASAction> actions, Error error) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onDeleteActionResult(boolean success, Error error) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onDeleteAllActionsResult(boolean success, Error error) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onIdentifySignatureResult(ASAction action, Error error) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onCompleteRecordSensorToTrainActionResult(final ASAction action, final Error error) {

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

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {

                        // Disable waiting cursor after get data from server
                        mLayout_progressbar.setVisibility(View.GONE);

                        if (mStrength == ASEngine.ASStrength.ASStrengthNoData) {
                            // Switch to 6th screen
                            if (mScreen == 5) {
                                mScreen = 6;  //#2
                                setScreen(mScreen);

                                // Set how many training times are still needed
                                mTextView_TrainingStatus.setText(getString((getIdentifier("textview_training_status", "string")), (mNumberOfSignatureStillNeed), mNumberOfSignatureStillNeed));
                            }
                        } else {
                            if (action.numberOfSignatureStillNeedBeforeVerify == 0) {

                                // Switch to 7th screen #2
                                mScreen = 7;
                                setScreen(mScreen);

                                // Save data
                                ArrayList<ASAction> actions = new ArrayList<ASAction>();
                                action.fingerPointX = mFingerPointX;
                                action.fingerPointY = mFingerPointY;
                                actions.add(action);

                                // Send data to AirSig Server
                                mASEngine.setActions(actions);
                            }
                        }

                    }
                });

            }

            if (null != error) {
                Log.i(TAG, "error.code: " + error.code);
                Log.i(TAG, "error.message: " + error.message);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {

                        // Disable waiting cursor after get data from server
                        mLayout_progressbar.setVisibility(View.GONE);

                        if (error.code == -6) {
                            // Switch to 6th screen
                            if (mScreen == 5) {

                                mScreen = 6;  //#3
                                setScreen(mScreen);

                                // Show error message
                                mTextView_TrainingStatus.setText(getPopupString(error.code));
                            }
                        } else {
                            mImg_ThumbPosition.setVisibility(View.VISIBLE);

                            // Popup error message
                            openPopupMsg(getPopupString(error.code));
                        }

                    }
                });
            }
        }

        @Override
        public void onCompleteRecordSensorToIdentifyActionResult(ASAction action, Error error) {
            Log.i(TAG, "\nonCompleteRecordSensorToIdentifyActionResult");
            if (null != action) {
                Log.i(TAG, "action.action: " + action.action);
                Log.i(TAG, "action.actionIndex: " + action.actionIndex);
                Log.i(TAG, "action.fingerPointX: " + action.fingerPointX);
                Log.i(TAG, "action.fingerPointY: " + action.fingerPointY);
                Log.i(TAG, "action.strength: " + action.strength);
                Log.i(TAG, "action.numberOfSignatureStillNeedBeforeVerify: " + action.numberOfSignatureStillNeedBeforeVerify);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        // Disable waiting cursor after get data from server
                        mLayout_progressbar.setVisibility(View.GONE);
                    }
                });
            }
            if (null != error) {
                Log.i(TAG, "error.code: " + error.code);
                Log.i(TAG, "error.message: " + error.message);
            }

        }

        @Override
        public void onResetSignatureResult(ASAction action, final Error error) {
            Log.i(TAG, "\nonResetSignatureResult");
            if (null != action) {
                Log.i(TAG, "action.action: " + action.action);
                Log.i(TAG, "action.actionIndex: " + action.actionIndex);
                Log.i(TAG, "action.fingerPointX: " + action.fingerPointX);
                Log.i(TAG, "action.fingerPointY: " + action.fingerPointY);
                Log.i(TAG, "action.strength: " + action.strength.getInt());
                Log.i(TAG, "action.numberOfSignatureStillNeedBeforeVerify: " + action.numberOfSignatureStillNeedBeforeVerify);

                mStrength = action.strength;

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        // Disable waiting cursor after get data from server
                        mLayout_progressbar.setVisibility(View.GONE);

                        if (mResetFlag) {
                            // Switch to 1st screen if reset successful
                            mScreen = 1;
                            setScreen(mScreen);

                            mResetFlag = false;
                        }
                    }
                });
            }
            if (null != error) {
                Log.i(TAG, "error.code: " + error.code);
                Log.i(TAG, "error.message: " + error.message);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        // Disable waiting cursor
                        mLayout_progressbar.setVisibility(View.GONE);

                        // Popup error message
                        openPopupMsg(getPopupString(error.code));
                    }
                });
            }
        }

        @Override
        public void onHttpError(final Error error) {
            Log.i(TAG, "onHttpError");
            if (null != error) {
                Log.i(TAG, "error.code: " + error.code);
                Log.i(TAG, "error.message: " + error.message);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        // Disable waiting cursor
                        mLayout_progressbar.setVisibility(View.GONE);

                        // Popup error message
                        openPopupMsg(getPopupString(error.code));
                    }
                });

            }
        }

    };

    public void getPreferences() {
        // Read parameter from local file
        mSharedPreferences = getSharedPreferences(SP_DATA_FILE, 0);

        // Get parameters
        mFingerSubPointX = mSharedPreferences.getFloat(SP_FIELD_SUB_X, 0);
        mFingerSubPointY = mSharedPreferences.getFloat(SP_FIELD_SUB_Y, 0);
    }

    // Used to save difference of coordinates
    private void savePreferences(float subX, float subY) {
        // Create local file to store parameters
        mSharedPreferences = getSharedPreferences(SP_DATA_FILE, 0);

        // Save parameters
        mSharedPreferences.edit()
                .putFloat(SP_FIELD_SUB_X, subX)
                .putFloat(SP_FIELD_SUB_Y, subY)
                .commit();
    }

    // Used to show training result to UI
    private void showTrainingResultToUI(ASStrength strength) {
        // Show training result
        switch (strength) {
            case ASStrengthStrong:
                mImgView_Background_screen7.setImageResource((getIdentifier("img_strengthlevel_04", "drawable")));
                //mImg_ThumbPosition.setImageResource((getIdentifier("ani_doubleclick_03", "drawable")));
                mTextView_TrainingResult.setText(getString((getIdentifier("textview_training_result_strong", "string"))));
                break;
            case ASStrengthNormal:
                mImgView_Background_screen7.setImageResource((getIdentifier("img_strengthlevel_03", "drawable")));
                mTextView_TrainingResult.setText(getString((getIdentifier("textview_training_result_normal", "string"))));
                break;
            case ASStrengthWeak:
                mImgView_Background_screen7.setImageResource((getIdentifier("img_strengthlevel_02", "drawable")));
                mTextView_TrainingResult.setText(getString((getIdentifier("textview_training_result_weak", "string"))));
                break;
            case ASStrengthWeakest:
                mImgView_Background_screen7.setImageResource((getIdentifier("img_strengthlevel_01", "drawable")));
                mTextView_TrainingResult.setText(getString((getIdentifier("textview_training_result_weakest", "string"))));
                break;
            default:
                break;
        }
    }

    private String getPopupString(int errorCode) {
        String msg = "";
        switch (errorCode) {
            case ASEngine.Error.NOT_FOUND:
                msg = getResources().getString((getIdentifier("err_not_found", "string")));
                break;
            case ASEngine.Error.INVALID_LICENSE:
                msg = getResources().getString((getIdentifier("err_invalid_license", "string")));
                break;
            case ASEngine.Error.INVALID_ACTION_INDEX:
                msg = getResources().getString((getIdentifier("err_invalid_action_index", "string")));
                break;
            case ASEngine.Error.INVALID_USER_ID:
                msg = getResources().getString((getIdentifier("err_user_id", "string")));
                break;
            case ASEngine.Error.INVALID_SIGNATURE:
                msg = getResources().getString((getIdentifier("err_invalid_signature", "string")));
                break;
            case ASEngine.Error.DIFFERENT_SIGNATURE:
                msg = getResources().getString((getIdentifier("err_high_defference", "string")));
                break;
            case ASEngine.Error.SENSOR_UNAVAILABLE:
                msg = getResources().getString((getIdentifier("err_no_sensor", "string")));
                break;
            case ASEngine.Error.INTERNET_UNAVAILABLE:
                msg = getResources().getString((getIdentifier("err_no_internet", "string")));
                break;
        }

        return msg;
    }

    // Pop up notification message
    private void openPopupMsg(String msg) {
        new AlertDialog.Builder(TrainingActivity.this)
                .setTitle(getResources().getString((getIdentifier("training_popup_title", "string"))))
                .setMessage(msg)
                .setPositiveButton(getResources().getString((getIdentifier("ok", "string"))),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialoginterface, int i) {
                                // TODO
                            }
                        }
                )
                .show();
    }
}
