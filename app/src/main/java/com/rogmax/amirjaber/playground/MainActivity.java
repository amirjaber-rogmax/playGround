package com.rogmax.amirjaber.playground;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.rogmax.amirjaber.playground.adapters.TestPagerAdapter;
import com.rogmax.amirjaber.playground.models.PagerDto;
import com.viewpagerindicator.CirclePageIndicator;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;


    //vars
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();


        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                URL server = new URL("http://unitestproj.tk");
                URLConnection connection = server.openConnection();
                connection.setConnectTimeout(3000);
                connection.connect();
                editor.putString("key_connected", "true").apply();
            } catch (Exception e) {
                editor.putString("key_connected", "false").apply();
            }
            String value = preferences.getString("key_connected", "");
            Log.d(TAG, "onCreate: Connection status: " + value);
        }, 3, 3, TimeUnit.SECONDS);

        if (isServicesOK()) {
            init();
        }

        Button getLocation = findViewById(R.id.get_location);
        getLocation.setOnClickListener(v -> {
            if ("true".equals(preferences.getString("key_connected", ""))) {

                GPStracker g = new GPStracker(getApplicationContext());
                Location l = g.getLocation();
                if (l != null) {
                    double lat = l.getLatitude();
                    double lon = l.getLongitude();
                    showLongToast("Latitude: " + lat + "\nLongitude: " + lon);
                }

                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                startActivity(intent);

            } else if ("false".equals(preferences.getString("key_connected", ""))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Could not connect to server!");
                builder.setNeutralButton("acknowledge", ((dialog, which) -> {
                    dialog.dismiss();
                }));
                builder.show();
            }


        });

        Button readMe = findViewById(R.id.read_me);
        readMe.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            @SuppressLint("InflateParams") final View readMeView = inflater.inflate(R.layout.pager_layout, null);
            builder.setView(readMeView);
            builder.setCancelable(true);

            List<PagerDto> pagerDtos = new ArrayList<>();
            pagerDtos.add(new PagerDto("1", "Android Manifest"));
            pagerDtos.add(new PagerDto("2", "Android Gradle"));
            pagerDtos.add(new PagerDto("3", "Android Adapters"));
            pagerDtos.add(new PagerDto("4", "Android Models"));
            pagerDtos.add(new PagerDto("5", "Android Retrofit"));
            pagerDtos.add(new PagerDto("6", "Android Activities"));
            pagerDtos.add(new PagerDto("7", "Android Resources"));

            TestPagerAdapter adapter = new TestPagerAdapter(this, pagerDtos);
            ViewPager pager = readMeView.findViewById(R.id.container);
            pager.setAdapter(adapter);
            CirclePageIndicator pageIndicator = readMeView.findViewById(R.id.page_indicator);
            pageIndicator.setViewPager(pager);
            pageIndicator.setCurrentItem(0);

            AlertDialog dialog = builder.create();
            dialog.show();

        });


    }

    private void init() {
        Button mapActivity = findViewById(R.id.map_button);
        mapActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });


    }


    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK(): checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error has occurred but we can resolve it
            Log.d(TAG, "isServicesOK(): an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void goToSite(View view) {
        goToUrl("http://unitestproj.tk/#");
    }

    private void goToUrl (String url){
        Uri uri = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(launchBrowser);
    }

    public void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
