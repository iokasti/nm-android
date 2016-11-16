package com.uoa.iokasti.networkmonitor.wifi;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uoa.iokasti.networkmonitor.R;

/**
 * Fragment
 * Information for a specific Wifi network
 * Layout xml: fragment_wifi_info.xml
 */
public class WifiInfoFragment extends Fragment {
    /*TODO CHANGE LAYOUT TO TABLE*/
    /* TextView Declarations */
    private TextView SSID_textview, BSSID_textview, capabilities_textview, RSSI_textview, channelWidth_textview,
            centerFreq0_textview, centerFreq1_textview, timestamp_textview, frequency_textview;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_wifi_info, container, false);

        /* Find Textviews (Layout xml: fragment_wifi_info.xml) */
        SSID_textview = (TextView) view.findViewById(R.id.SSID_textview);
        BSSID_textview = (TextView) view.findViewById(R.id.BSSID_textview);
        capabilities_textview = (TextView) view.findViewById(R.id.capabilities_textview);
        RSSI_textview = (TextView) view.findViewById(R.id.RSSI_textview);
        channelWidth_textview = (TextView) view.findViewById(R.id.channelWidth_textview);
        centerFreq0_textview = (TextView) view.findViewById(R.id.centerFreq0_textview);
        centerFreq1_textview = (TextView) view.findViewById(R.id.centerFreq1_textview);
        frequency_textview = (TextView) view.findViewById(R.id.frequency_textview);

        /* Get Bundle from arguments */
        Bundle selected_wifi_info = getArguments();

        /* Set textviews content from received info in the Bundle */
        SSID_textview.setText(String.format("SSID: %s", selected_wifi_info.getString("SSID")));
        BSSID_textview.setText(String.format("BSSID: %s", selected_wifi_info.getString("BSSID")));
        capabilities_textview.setText(String.format("Capabilities: %s", selected_wifi_info.getString("capabilities")));
        channelWidth_textview.setText(String.format("Channel Width: %s", selected_wifi_info.getString("channelWidth")));
        centerFreq0_textview.setText(String.format("Center Freq 0: %s", selected_wifi_info.getString("centerFreq0")));
        centerFreq1_textview.setText(String.format("Center Freq 1: %s", selected_wifi_info.getString("centerFreq1")));
        frequency_textview.setText(String.format("Frequency: %s", selected_wifi_info.getString("frequency")));
        RSSI_textview.setText(String.format("RSSI: %s", selected_wifi_info.getString("RSSI")));
        return view;
    }
}
