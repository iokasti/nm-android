package com.uoa.iokasti.networkmonitor.cell;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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
import com.uoa.iokasti.networkmonitor.R;
import com.uoa.iokasti.networkmonitor.entities._CellInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectedCellFragment extends Fragment {

    private TableLayout cellInfoTableLayout;
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

    private MapView connectedCellMapView;
    private GoogleMap connectedCellGoogleMap;


    //  private String openCellIdApiKey = "ef445193-fc82-482f-b199-9422b79a0e0a"; original
    private String openCellIdApiKey = "5712903e-fe28-4a66-a0c1-bd496178783f"; // temp

    int oldCellId = Integer.MAX_VALUE;
    private _CellInfo connectedCellInfo;

    /* TODO add as setting to user, save to db */
    static int cellInfoScanInterval = 2500;
//    Timer timer = null;
    static TimerTask cellInfoScanTask = null;

    /* TODO check for internet connection */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connected_cell, container, false);
        findViews(view);

        /* map initialization */
        connectedCellMapView.onCreate(savedInstanceState);
        connectedCellMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        connectedCellInfo = new _CellInfo();

        // update cell info here
        cellInfoScanTask = new TimerTask() {
            @Override
            public void run() {
                new cellInfoScan().execute();
            }
        };
//        timer = new Timer();
//        timer.scheduleAtFixedRate(cellInfoScanTask, 0, cellInfoScanInterval);

        return view;
    }

    private void findViews(View view) {
        cellInfoTableLayout = (TableLayout) view.findViewById(R.id.cell_info_table);
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
        connectedCellMapView = (MapView) view.findViewById(R.id.connected_cell_mapview);
    }

    @Override
    public void onResume() {
        super.onResume();
        connectedCellMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        connectedCellMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectedCellMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        connectedCellMapView.onLowMemory();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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


    public class cellInfoScan extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.d("connected Cell", "timer");
            List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();
            for (CellInfo cellInfo : cellInfos) {
                if (cellInfo.isRegistered()) {
                    if (cellInfo instanceof CellInfoGsm) {
                    } else if (cellInfo instanceof CellInfoCdma) {
                    } else if (cellInfo instanceof CellInfoWcdma) {
                    } else if (cellInfo instanceof CellInfoLte) {
                        CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                        connectedCellInfo.setCellId(cellInfoLte.getCellIdentity().getCi());
                        connectedCellInfo.setTac(cellInfoLte.getCellIdentity().getTac());
                        connectedCellInfo.setLac(cellInfoLte.getCellIdentity().getTac());
                        connectedCellInfo.setRssi(getRssi(cellInfoLte.getCellSignalStrength().toString()));
                        connectedCellInfo.setRsrp(cellInfoLte.getCellSignalStrength().getDbm());
                        connectedCellInfo.setRsrq(getRsrq(cellInfoLte.getCellSignalStrength().toString()));
                        connectedCellInfo.setRssnr(getRssnr(cellInfoLte.getCellSignalStrength().toString()));
                        connectedCellInfo.setCqi(getCqi(cellInfoLte.getCellSignalStrength().toString()));
                        connectedCellInfo.setMcc(cellInfoLte.getCellIdentity().getMcc());
                        connectedCellInfo.setMnc(cellInfoLte.getCellIdentity().getMnc());
                        connectedCellInfo.setRat("LTE");

                        if (oldCellId != connectedCellInfo.getCellId() || connectedCellInfo.getLatitude() == -1) {
                            double[] latLong = getLatLong(connectedCellInfo.getCellId(), connectedCellInfo.getMcc(), connectedCellInfo.getMnc(), connectedCellInfo.getTac());
                            connectedCellInfo.setLatitude(latLong[0]);
                            connectedCellInfo.setLongitude(latLong[1]);
                        }
                        break;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isDetached()) {
                cellIdTextview.setText(String.valueOf(connectedCellInfo.getCellId()));
                tacTextview.setText(String.valueOf(connectedCellInfo.getTac()));
                lacTextview.setText(String.valueOf(connectedCellInfo.getLac()));
                rssiTextview.setText(String.valueOf(connectedCellInfo.getRssi()));
                rsrpTextview.setText(String.valueOf(connectedCellInfo.getRsrp()));
                rsrqTextview.setText(String.valueOf(connectedCellInfo.getRsrq()));
                rssnrTextview.setText(String.valueOf(connectedCellInfo.getRssnr()));
                cqiTextview.setText(String.valueOf(connectedCellInfo.getCqi()));
                ratTextview.setText(connectedCellInfo.getRat());
                mccTextview.setText(String.valueOf(connectedCellInfo.getMcc()));
                mncTextview.setText(String.valueOf(connectedCellInfo.getMnc()));
                if (oldCellId != connectedCellInfo.getCellId()) {
                    connectedCellMapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap mMap) {
                            connectedCellGoogleMap = mMap;

                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            connectedCellGoogleMap.setMyLocationEnabled(true);

                            if (connectedCellInfo.getLatitude() != -1) {
                                // For dropping a marker at a point on the Map
                                LatLng cellLocation = new LatLng(connectedCellInfo.getLatitude(), connectedCellInfo.getLongitude());
                                connectedCellGoogleMap.addMarker(new MarkerOptions().position(cellLocation).title("Connected Cell #" + connectedCellInfo.getCellId()).snippet("Location of currently used cell."));

                                // For zooming automatically to the location of the marker
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(cellLocation).zoom(16).build();
                                connectedCellGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }
                        }
                    });
                    oldCellId = connectedCellInfo.getCellId();
                }
            }
        }
    }


    private double[] getLatLong(int cellId, int mcc, int mnc, int tac) {
        String sURL = String.format("http://opencellid.org/cell/get?key=%s&mcc=%d&mnc=%d&cellid=%d&lac=%d&format=json", openCellIdApiKey, mcc, mnc, cellId, tac);
        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            // Convert to a JSON object
            JsonParser jsonParser = new JsonParser(); //from gson
            JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject openCellIdJson = root.getAsJsonObject(); //May be an array, may be an object.
            if (!openCellIdJson.entrySet().contains("error")) {
                double latitude = openCellIdJson.get("lat").getAsDouble();
                double longitude = openCellIdJson.get("lon").getAsDouble();
                return new double[]{latitude, longitude};
            } else {
                return new double[]{-1, -1};
            }
        } catch (IOException e) {
            return new double[]{-1, -1};
        }
    }
}
