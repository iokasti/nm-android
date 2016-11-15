package com.uoa.iokasti.networkmonitor;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity
 * Information for Wifi networks
 * Layout xml: activity_wifi_networks.xml
 */
public class WifiActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView wifiNetworksList;

    /* Android classes/function to get information about wifi */
    private WifiManager wifi;
    private WifiBroadcastReceiver wifiBroadcastReceiver;

    /* ListView Adapter Declaration */
    private ArrayAdapter<String> wifiNetworksListAdapter;

    /* Structures used to save wifi networks information */
    private HashMap<String, ScanResult> wifiNetworksMap;
    private ArrayList<String> wifiNetworksNames;

    /* TODO add as setting to user, save to db */
    private int wifiScanInterval = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        findViews();

        /* Get the WiFiManager */
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
            /* If wifi is disabled created a pop up "Enabling" and enable it */
            Toast.makeText(getApplicationContext(), "Wifi is disabled. Enabling...",
                    Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        /* Initialize structures and the adapter */
        wifiNetworksNames = new ArrayList<String>();
        wifiNetworksListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wifiNetworksNames);
        wifiNetworksList.setAdapter(wifiNetworksListAdapter);
        wifiBroadcastReceiver = new WifiBroadcastReceiver();
        wifiNetworksMap = new HashMap<String, ScanResult>();

        /* Register a receiver to run when scan results are available */
        registerReceiver(wifiBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        /* Scan for wifi networks every wifiScanInterval seconds*/
        TimerTask wifi_scan_task = new TimerTask() {
            @Override
            public void run() {
                new wifiScanAsync().execute();
            }
        };
        new Timer().scheduleAtFixedRate(wifi_scan_task, 0, wifiScanInterval);
    }

    protected void onPause() {
        /* Unregister the receiver on pause */
        unregisterReceiver(wifiBroadcastReceiver);
        super.onPause();
    }

    protected void onResume() {
        /* Register the receiver again on resume */
        registerReceiver(wifiBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /* When a List item is pressed open a WifiInfoFragment containing specific network's information */

        /* Find which clicked Item information */
        String clickedWifiInfoString = wifiNetworksList.getItemAtPosition(position).toString();
        String clickedWifiInfoBSSID = clickedWifiInfoString.substring(0, clickedWifiInfoString.indexOf("\n")).replaceAll("\\s+", "");

        /* Create a Bundle so we can pass it to the fragment as an argument*/
        Bundle selectedWifiInfo = new Bundle();

        /* Add information to the Bundle */
        selectedWifiInfo.putString("BSSID", wifiNetworksMap.get(clickedWifiInfoBSSID).BSSID);
        selectedWifiInfo.putString("SSID", wifiNetworksMap.get(clickedWifiInfoBSSID).SSID);
        selectedWifiInfo.putString("capabilities", wifiNetworksMap.get(clickedWifiInfoBSSID).capabilities);
        selectedWifiInfo.putString("frequency", Integer.toString(wifiNetworksMap.get(clickedWifiInfoBSSID).frequency));
        /* Some information are not available if SDK <= 23 */
        if (Build.VERSION.SDK_INT >= 23) {
            selectedWifiInfo.putString("channelWidth", Integer.toString(wifiNetworksMap.get(clickedWifiInfoBSSID).channelWidth));
            selectedWifiInfo.putString("centerFreq0", Integer.toString(wifiNetworksMap.get(clickedWifiInfoBSSID).centerFreq0));
            selectedWifiInfo.putString("centerFreq1", Integer.toString(wifiNetworksMap.get(clickedWifiInfoBSSID).centerFreq1));
        } else {
            Toast.makeText(getApplicationContext(), "Could not display all information due to SDK < 23.",
                    Toast.LENGTH_LONG).show();
        }
        selectedWifiInfo.putString("RSSI", Integer.toString(wifiNetworksMap.get(clickedWifiInfoBSSID).level));

        /* Create a new Fragment and open it */
        Fragment wifiInfoFragment = new WifiInfoFragment();
        wifiInfoFragment.setArguments(selectedWifiInfo);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.wifi_info_fragment_container, wifiInfoFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    class WifiBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            /* BUG ON WifiManager#getScanResults(): for Nexus devices
             * https://code.google.com/p/android/issues/detail?id=185370
             * NEEDS GPS TO ACCESS SCAN RESULTS */
            /* Wifi scan results are available so fill the data structures and the listview*/
            List<ScanResult> wifiNetworksList = wifi.getScanResults();
            wifiNetworksNames.clear();
            wifiNetworksListAdapter.clear();
            wifiNetworksMap.clear();
            for (ScanResult sr : wifiNetworksList) {
                wifiNetworksNames.add(sr.BSSID + "\n" + sr.SSID);
                wifiNetworksMap.put(sr.BSSID, sr);
            }
            wifiNetworksListAdapter.notifyDataSetChanged();
        }
    }

    public class wifiScanAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            registerReceiver(wifiBroadcastReceiver, new IntentFilter(
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifi.startScan();
            return null;
        }
    }

    private void findViews() {
        /* Find Listview (Layout xml: activity_wifi_networks.xml) */
        wifiNetworksList = (ListView)findViewById( R.id.wifi_networks_list );

        /* Set Click Listener for List Item */
        wifiNetworksList.setOnItemClickListener(this);
    }
}
