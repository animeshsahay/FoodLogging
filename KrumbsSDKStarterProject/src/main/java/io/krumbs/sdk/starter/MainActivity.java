/*
 * Copyright (c) 2016 Krumbs Inc
 * All rights reserved.
 *
 */
package io.krumbs.sdk.starter;

import com.google.android.gms.maps.MapView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Map;

import io.krumbs.sdk.KrumbsSDK;
import io.krumbs.sdk.dashboard.KDashboardFragment;
import io.krumbs.sdk.dashboard.KGadgetDataTimePeriod;
import io.krumbs.sdk.dashboard.KGadgetType;
import io.krumbs.sdk.data.model.Event;
import io.krumbs.sdk.krumbscapture.KCaptureCompleteListener;
import io.krumbs.sdk.krumbscapture.settings.KUserPreferences;


public class MainActivity extends BaseAvtivity {
    //private KGadgetDataTimePeriod defaultInitialTimePeriod = KGadgetDataTimePeriod.TODAY;
    private KDashboardFragment kDashboard;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageButton startCaptureButton;
    ImageView hamburgerIV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applyFont(MainActivity.this, findViewById(R.id.base_layout));
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        CheckEnableGPS();
        //if (savedInstanceState == null) {
          //  kDashboard = buildDashboard();
          //  getSupportFragmentManager().beginTransaction().replace(R.id.content, kDashboard).commit();
        //}

        startCaptureButton = (ImageButton)findViewById(R.id.start_report_button);
        hamburgerIV = (ImageView)findViewById(R.id.hamburger);
        drawerLayout = (DrawerLayout)findViewById(R.id.base_layout);
        navigationView  = (NavigationView)findViewById(R.id.navigation_drawer);


        hamburgerIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else{
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        KrumbsSDK.setUserPreferences(
                new KUserPreferences.KUserPreferencesBuilder().audioRecordingEnabled(true).build());
        if (startCaptureButton != null) {
            startCaptureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startCapture();
                }
            });
        }

        View view = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.aboutus:
                        Toast.makeText(MainActivity.this,"Clicked",Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }

                return true;
            }
        });


    }

    private void CheckEnableGPS() {
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED );
        if(!provider.equals("")){
            //GPS Enabled

        }else{
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            Toast.makeText(MainActivity.this, "Location is not enable",
                    Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
    }


    private void startCapture() {
        int containerId = R.id.camera_container;
// SDK usage step 4 - Start the K-Capture component and add a listener to handle returned images and URLs
        KrumbsSDK.startCapture(containerId, this, new KCaptureCompleteListener() {
            @Override
            public void captureCompleted(CompletionState completionState, boolean audioCaptured,
                                         Map<String, Object> map) {
                if (completionState != null) {
                    Log.i("KRUMBS-CALLBACK", "STATUS" + ": " + completionState.toString());
                }
                if (completionState == CompletionState.CAPTURE_SUCCESS) {
// The local image url for your capture
                    String imagePath = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_IMAGE_PATH);
                    if (audioCaptured) {
// The local audio url for your capture (if user decided to record audio)
                        String audioPath = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_AUDIO_PATH);
                        Log.i("KRUMBS-CALLBACK", audioPath);
                    }
// The mediaJSON url for your capture
                    String mediaJSONUrl = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_JSON_URL);
                    Log.i("KRUMBS-CALLBACK", mediaJSONUrl + ", " + imagePath);
                    if (map.containsKey(KCaptureCompleteListener.CAPTURE_EVENT)) {
                        Event ev = (Event) map.get(KCaptureCompleteListener.CAPTURE_EVENT);
                        Log.i("KRUMBS-CALLBACK", "Event captured =  + " + ev.objectId());
                    }
                } else if (completionState == CompletionState.CAPTURE_CANCELLED ||
                        completionState == CompletionState.SDK_NOT_INITIALIZED) {
                }
            }
        });
    }

    //    http://stackoverflow.com/questions/7469082/getting-exception-illegalstateexception-can-not-perform-this-action-after-onsa
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

}
