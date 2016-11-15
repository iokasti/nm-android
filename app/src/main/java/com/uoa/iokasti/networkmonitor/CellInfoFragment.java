package com.uoa.iokasti.networkmonitor;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CellInfoFragment extends Fragment {

    private TableLayout cellInfoTableTableLayout;
    private TextView cellIdTextview;
    private TextView lacTextview;
    private TextView tacTextview;
    private TextView rssiTextview;
    private TextView rsrpTextview;
    private TextView rsrqTextview;
    private TextView rssnrTextview;
    private TextView cqiTextview;
    private TextView ratTextview;
    private TextView mccTextview;
    private TextView mncTextview;
    private TelephonyManager telephonyManager;

    private double latitude;
    private double longitude;

    MapView mMapView;
    private GoogleMap googleMap;

    String openCellIdApiKey = "ef445193-fc82-482f-b199-9422b79a0e0a";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cell_info, container, false);
        findViews(view);

        /* map initialization */
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfos = (List<CellInfo>) telephonyManager.getAllCellInfo();
        int cellId = Integer.MAX_VALUE;
        int lac = Integer.MAX_VALUE;
        int tac = Integer.MAX_VALUE;
        int rssi = Integer.MAX_VALUE;
        int rsrp = Integer.MAX_VALUE;
        int rsrq = Integer.MAX_VALUE;
        int rssnr = Integer.MAX_VALUE;
        int cqi = Integer.MAX_VALUE;
        String rat = "";
        int mcc = Integer.MAX_VALUE;
        int mnc = Integer.MAX_VALUE;
        for (CellInfo cellInfo : cellInfos) {
            if (cellInfo.isRegistered()) {
                if (cellInfo instanceof CellInfoGsm) {
                } else if (cellInfo instanceof CellInfoCdma) {
                } else if (cellInfo instanceof CellInfoWcdma) {
                } else if (cellInfo instanceof CellInfoLte) {
                    CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                    cellId = cellInfoLte.getCellIdentity().getCi();
                    cellInfoTableTableLayout.removeView(view.findViewById(R.id.lac_row));
                    tac = cellInfoLte.getCellIdentity().getTac();
                    rssi = getRssi(cellInfoLte.getCellSignalStrength().toString());
                    rsrp = cellInfoLte.getCellSignalStrength().getDbm();
                    rsrq = getRsrq(cellInfoLte.getCellSignalStrength().toString());
                    rssnr = getRssnr(cellInfoLte.getCellSignalStrength().toString());
                    cqi = getCqi(cellInfoLte.getCellSignalStrength().toString());
                    rat = "LTE";
                    mcc = cellInfoLte.getCellIdentity().getMcc();
                    mnc = cellInfoLte.getCellIdentity().getMnc();
                    break;
                }
            }
        }

        cellIdTextview.setText(String.valueOf(cellId));
        tacTextview.setText(String.valueOf(tac));
        rssiTextview.setText(String.valueOf(rssi));
        rsrpTextview.setText(String.valueOf(rsrp));
        rsrqTextview.setText(String.valueOf(rsrq));
        rssnrTextview.setText(String.valueOf(rssnr));
        cqiTextview.setText(String.valueOf(cqi));
        ratTextview.setText(rat);
        mccTextview.setText(String.valueOf(mcc));
        mncTextview.setText(String.valueOf(mnc));

        final int finalCellId = cellId;
        final int finalMcc = mcc;
        final int finalMnc = mnc;
        final int finalTac = tac;
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                try {
                    new setLatLongAsync().execute(finalCellId, finalMcc, finalMnc, finalTac).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                // For dropping a marker at a point on the Map
                LatLng cellLocation = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(cellLocation).title("Connected Cell #" + finalCellId).snippet("Location of currently used cell."));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(cellLocation).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        return view;
    }

    private void findViews(View view) {
        cellInfoTableTableLayout = (TableLayout) view.findViewById(R.id.cell_info_table);
        cellIdTextview = (TextView) view.findViewById(R.id.cell_id_textview);
        lacTextview = (TextView) view.findViewById(R.id.lac_textview);
        tacTextview = (TextView) view.findViewById(R.id.tac_textview);
        rssiTextview = (TextView) view.findViewById(R.id.rssi_textview);
        rsrpTextview = (TextView) view.findViewById(R.id.rsrp_textview);
        rsrqTextview = (TextView) view.findViewById(R.id.rsrq_textview);
        rssnrTextview = (TextView) view.findViewById(R.id.rssnr_textview);
        cqiTextview = (TextView) view.findViewById(R.id.cqi_textview);
        ratTextview = (TextView) view.findViewById(R.id.rat_textview);
        mccTextview = (TextView) view.findViewById(R.id.mcc_textview);
        mncTextview = (TextView) view.findViewById(R.id.mnc_textview);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private int getRssi(String CellSignalStrengthLte) {
        return Integer.valueOf(CellSignalStrengthLte.substring(CellSignalStrengthLte.indexOf("ss=") + 3, CellSignalStrengthLte.indexOf("rsrp") - 1));
    }

    private int getRsrq(String CellSignalStrengthLte) {
        return Integer.valueOf(CellSignalStrengthLte.substring(CellSignalStrengthLte.indexOf("rsrq=") + 5, CellSignalStrengthLte.indexOf("rssnr") - 1));
    }

    private int getRssnr(String CellSignalStrengthLte) {
        return Integer.valueOf(CellSignalStrengthLte.substring(CellSignalStrengthLte.indexOf("rssnr=") + 6, CellSignalStrengthLte.indexOf("cqi") - 1));
    }

    private int getCqi(String CellSignalStrengthLte) {
        return Integer.valueOf(CellSignalStrengthLte.substring(CellSignalStrengthLte.indexOf("cqi=") + 4, CellSignalStrengthLte.indexOf("ta") - 1));
    }


    public class setLatLongAsync extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            int cellId = params[0];
            int mcc = params[1];
            int mnc = params[2];
            int tac = params[3];
            String sURL = String.format("http://opencellid.org/cell/get?key=ef445193-fc82-482f-b199-9422b79a0e0a&mcc=%d&mnc=%d&cellid=%d&lac=%d&format=json", mcc, mnc, cellId, tac);
            try {
                URL url = new URL(sURL);
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();

                // Convert to a JSON object
                JsonParser jp = new JsonParser(); //from gson
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
                JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                latitude = rootobj.get("lat").getAsDouble();
                longitude = rootobj.get("lon").getAsDouble();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
