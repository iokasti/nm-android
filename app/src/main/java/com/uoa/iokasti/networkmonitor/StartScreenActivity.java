package com.uoa.iokasti.networkmonitor;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.uoa.iokasti.networkmonitor.cell.CellActivity;
import com.uoa.iokasti.networkmonitor.wifi.WifiActivity;

import java.util.Locale;

/**
 * Activity
 * Application start screen
 * Layout xml: activity_start_screen.xml
 * Contains 2 Buttons for navigation to:
 * - Mobile Network Information
 * - Wifi Networks Information
 */
public class StartScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mobileNetworkButton;
    private Button wifiNetworksButton;

    /* int Declaration for use with Permissions check/request */
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        findViews();

        if (Build.VERSION.SDK_INT >= 23)
            if (!checkPermission())
                requestPermission();

        // change app locale
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Locale locale = new Locale("en_US");
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        resources.updateConfiguration(configuration, displayMetrics);

        turnLocationOn();
        turnWifiOn();
    }

    @Override
    public void onClick(View view) {
        if (!checkPermission())
            requestPermission();
        else
            switch (view.getId()) {
                case R.id.mobile_network_button:
                /* Change StartScreenActivity to MobileNetworksActivity */
                    Intent intentMobileNetwork = new Intent(StartScreenActivity.this, CellActivity.class);
                    startActivity(intentMobileNetwork);
                    break;
                case R.id.wifi_networks_button:
                /* Change StartScreenActivity to WifiActivity */
                    Intent intentWifiNetworks = new Intent(StartScreenActivity.this, WifiActivity.class);
                    startActivity(intentWifiNetworks);
                    break;
                default:
                    break;
            }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    @Override
    /* After user grants or denies permissions this function will run */
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length <= 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // permission denied
                Toast.makeText(getApplicationContext(), "Please accept permission check for ACCESS_FINE_LOCATION to continue.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void findViews() {
        /* Find Buttons (Layout xml: activity_start_screen.xml) */
        mobileNetworkButton = (Button) findViewById(R.id.mobile_network_button);
        wifiNetworksButton = (Button) findViewById(R.id.wifi_networks_button);

        /* Set Click Listeners for Buttons */
        mobileNetworkButton.setOnClickListener(this);
        wifiNetworksButton.setOnClickListener(this);
    }

    private void turnWifiOn() {
        final WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifi.isWifiEnabled()) {
            // WiFi is enabled
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable WiFi");  // GPS not found
            builder.setMessage("The app needs WiFi to be enabled it? ");
            builder.setPositiveButton("Enable WiFi", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    wifi.setWifiEnabled(true);
                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.create().show();
            return;
        }
    }


    private void turnLocationOn() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is enabled
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable GPS");  // GPS not found
            builder.setMessage("The app needs GPS to be enabled do you want to enable it in the settings? ");
            builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.create().show();
            return;
        }
    }

}
