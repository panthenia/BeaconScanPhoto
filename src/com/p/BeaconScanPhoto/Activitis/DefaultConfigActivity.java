package com.p.BeaconScanPhoto.Activitis;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.TextView;
import com.p.BeaconScanPhoto.DataType.PublicData;
import com.p.BeaconScanPhoto.R;

/**
 * Created by p on 2015/6/29.
 */
public class DefaultConfigActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getActionBar();
        if (bar != null)
            bar.setTitle("设置");
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.WHITE);
            }
        }

        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.backcolor_norock));
        addPreferencesFromResource(R.xml.config);

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        String val = sharedPreferences.getString(s,"");
        if (s.contains("beacon_location")){
            if (val.equals("local")){
                Log.d("locating","local");
                PublicData.getInstance().setLocationType(PublicData.LOCATE_LOCAL);
            }else {
                Log.d("locating","gps");
                PublicData.getInstance().setLocationType(PublicData.LOCATE_GPS);
            }
        }else if (s.contains("beacon_sumury")){

        }else if (s.contains("beacon_scan_period")){
            int tval;
            try {
                tval = Integer.valueOf(val);
            }catch (NumberFormatException e){
                tval = 1000;
            }
            PublicData.getInstance().beaconScanPeriod = tval;
        }else if (s.contains("gps_scan_period")){
            int tval;
            try {
                tval = Integer.valueOf(val);
            }catch (NumberFormatException e){
                tval = 1000;
            }
            PublicData.getInstance().gpsScanPeriod = tval;
        }else if (s.contains("beacon_expiration_period")){
            int tval;
            try {
                tval = Integer.valueOf(val);
            }catch (NumberFormatException e){
                tval = 2000;
            }
            PublicData.getInstance().beaconExpirationPeriod = tval;
        }

    }
}