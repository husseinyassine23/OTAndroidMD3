package com.ids.fixot.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.classes.ShakeDetector;

import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Amal on 4/6/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    RelativeLayout rlLayout, changePassword, layoutFingerPrint, layoutVersionNumber, layoutPushNotification;
    Switch switchNot, switchFingerprint , switchDarckTheme;
    ImageView ivArrow;
    RadioButton rbArabic, rbEnglish;
    TextView tvVersionNumber;
    String versionNumber = "";

    FingerprintManager fingerprintManager;
    KeyguardManager keyguardManager;
    RelativeLayout layoutThemes;

    public SettingsActivity() {
        LocalUtils.updateConfig(this);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Actions.setActivityTheme(this);
        super.onCreate(savedInstanceState);
        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_setting);
        Actions.initializeBugsTracking(this);

        findViews();

        setListeners();

        Actions.initializeToolBar(getString(R.string.settings), SettingsActivity.this);
        Actions.overrideFonts(this, rlLayout, false);
        Actions.showHideFooter(this);

        rbArabic.setTypeface(MyApplication.droidbold);
    }

    private void findViews() {

        ivArrow = findViewById(R.id.ivArrow);
        tvVersionNumber =  findViewById(R.id.tvVersionNumber);
        layoutVersionNumber = findViewById(R.id.layoutVersionNumber);
        layoutFingerPrint = findViewById(R.id.layoutFingerPrint);
        layoutPushNotification = findViewById(R.id.layoutPushNotification);
        changePassword = findViewById(R.id.changePassword);
        rlLayout = findViewById(R.id.rlLayout);
        switchNot = findViewById(R.id.switchNot);
        switchFingerprint = findViewById(R.id.switchFingerprint);
        switchDarckTheme = findViewById(R.id.switchDarckTheme);
        rbArabic =  findViewById(R.id.rbArabic);
        rbEnglish =  findViewById(R.id.rbEnglish);
        layoutThemes =  findViewById(R.id.layoutThemes);

        /*changePassword.setVisibility(View.VISIBLE);
        ivArrow.setRotation(MyApplication.lang == MyApplication.ENGLISH ? 180f : 0f);*/

        try {

            versionNumber = getPackageManager().getPackageInfo(getPackageName(), 0).versionName + " (" + getPackageManager().getPackageInfo(getPackageName(), 0).versionCode + ")";

            tvVersionNumber.setText(versionNumber);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        switchFingerprint.setChecked(MyApplication.mshared.getBoolean(getResources().getString(R.string.allow_finger_print), false));
        switchDarckTheme.setChecked(!MyApplication.mshared.getBoolean(getResources().getString(R.string.normal_theme), true));

//        layoutThemes.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            try {
                if (fingerprintManager.isHardwareDetected()) {

                    if (fingerprintManager.hasEnrolledFingerprints()) {

                        if (keyguardManager.isKeyguardSecure()) {
                            //check if allowed and Remembered
                            layoutFingerPrint.setVisibility(View.VISIBLE);
                            layoutVersionNumber.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this, R.color.colorLight));
                            layoutVersionNumber.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this, MyApplication.mshared.getBoolean(SettingsActivity.this.getResources().getString(R.string.normal_theme), true) ?  R.color.colorLight  : R.color.colorLightTheme));

                        } else{

                            layoutFingerPrint.setVisibility(View.GONE);
                        }
                    }
                } else {

                    layoutFingerPrint.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                layoutFingerPrint.setVisibility(View.GONE);
            }
        } else {
            layoutFingerPrint.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListeners() {

        if (MyApplication.lang == MyApplication.ARABIC)
            rbArabic.setChecked(true);
        else
            rbEnglish.setChecked(true);


        rbArabic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                MyApplication.instruments.clear();
                Actions.setLocal(MyApplication.ARABIC, SettingsActivity.this);
                MyApplication.lang = MyApplication.ARABIC;
                MyApplication.editor.putInt("lang", MyApplication.ARABIC).apply();
                LocalUtils.setLocale(new Locale("ar"));
                LocalUtils.updateConfig(getApplication(), getBaseContext().getResources().getConfiguration());

                Intent intent = new Intent(MyApplication.class.getName() + "ChangedLanguage");
                LocalBroadcastManager.getInstance(SettingsActivity.this).sendBroadcast(intent);

                SettingsActivity.this.recreate();

            }
        });

        rbEnglish.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                MyApplication.instruments.clear();
                Actions.setLocal(MyApplication.ENGLISH, SettingsActivity.this);
                MyApplication.lang = MyApplication.ENGLISH;
                MyApplication.editor.putInt("lang", MyApplication.ENGLISH).apply();
                LocalUtils.setLocale(new Locale("en"));
                LocalUtils.updateConfig(getApplication(), getBaseContext().getResources().getConfiguration());

                Intent intent = new Intent(MyApplication.class.getName() + "ChangedLanguage");
                LocalBroadcastManager.getInstance(SettingsActivity.this).sendBroadcast(intent);

                SettingsActivity.this.recreate();
            }
        });
        layoutPushNotification.setOnClickListener(view -> switchNot.performClick());


        switchFingerprint.setOnClickListener(v -> {

            Log.wtf("Clicked on switch", "now");
            if (switchFingerprint.isChecked()) {
                MyApplication.editor.putBoolean(getResources().getString(R.string.allow_finger_print), true).apply();
            } else {

                MyApplication.editor.putBoolean(getResources().getString(R.string.allow_finger_print), false).apply();
            }
        });
        layoutFingerPrint.setOnClickListener(view -> switchFingerprint.performClick());


        switchDarckTheme.setOnClickListener(v -> {
            Log.wtf("Clicked on switchDarckTheme", "now");
            if (switchDarckTheme.isChecked()) {
                MyApplication.editor.putBoolean(getResources().getString(R.string.normal_theme), false).apply();
            } else {
                MyApplication.editor.putBoolean(getResources().getString(R.string.normal_theme), true).apply();
            }
            SettingsActivity.this.recreate();
        });
    }

    public void back(View v) {

        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Actions.setActivityTheme(this);
        //finish();
        //startActivity(getIntent());

        Actions.checkSession(this);
        //Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        Actions.InitializeMarketServiceV2(this);

    }

    @Override
    protected void onStop() {
        super.onStop();

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }
}
