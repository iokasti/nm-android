package com.uoa.iokasti.networkmonitor.cell;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.uoa.iokasti.networkmonitor.R;
import com.uoa.iokasti.networkmonitor.entities._CellInfo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CellsMapFragment extends Fragment {
    private MapView neighbourCellsMapView;
    private GoogleMap neighbourCellsGoogleMap;

    /* TODO add as setting to user, save to db */
    private final int neighbourCellsMapScanInterval = 10000;

    private Timer neighourCellsScanTaskTimer;
    private TimerTask neighbourCellsMapScanTask = null;

    static ArrayList<_CellInfo> neighbourCells = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cells_map, container, false);
        findViews(view);

        /* map initialization */
        neighbourCellsMapView.onCreate(savedInstanceState);
        neighbourCellsMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        neighourCellsScanTaskTimer = new Timer();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void findViews(View view) {
        neighbourCellsMapView = (MapView) view.findViewById(R.id.cells_mapview);
    }

    @Override
    public void onResume() {
        super.onResume();
        // update cell info here
        neighbourCellsMapScanTask = new TimerTask() {
            @Override
            public void run() {
                new neighbourCellsMapScan().execute();
            }
        };
        neighourCellsScanTaskTimer.scheduleAtFixedRate(neighbourCellsMapScanTask, 0, neighbourCellsMapScanInterval);

        neighbourCellsMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        neighbourCellsMapScanTask.cancel();
        neighourCellsScanTaskTimer.cancel();
        neighourCellsScanTaskTimer.purge();
        neighbourCellsMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        neighbourCellsMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        neighbourCellsMapView.onLowMemory();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    /* TODO need to move it to different file */
    private class neighbourCellsMapScan extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            getNeighbourCells();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            /* update neighbour cells list view in NeighbourCellsFragment, O.o */
            NeighbourCellsFragment.neighbourCellsIdList.clear();
            NeighbourCellsFragment.neighbourCellsListAdapter.clear();
            for (_CellInfo cell : neighbourCells) {
                NeighbourCellsFragment.neighbourCellsIdList.add(String.valueOf(cell.getCellId()));
            }
            NeighbourCellsFragment.neighbourCellsListAdapter.notifyDataSetChanged();

                /* continues normally */
            if (!isDetached()) {
                neighbourCellsMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        neighbourCellsGoogleMap = mMap;

                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        neighbourCellsGoogleMap.setMyLocationEnabled(true);
                        for (_CellInfo cell : neighbourCells) {
                            if (cell.getLatitude() != -1) {
                                // For dropping a marker at a point on the Map
                                LatLng cellLocation = new LatLng(cell.getLatitude(), cell.getLongitude());
                                neighbourCellsGoogleMap.addMarker(new MarkerOptions().position(cellLocation).title("Connected Cell #" + cell.getCellId())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_media_route_connecting_00_light)));


                                // For zooming automatically to the location of the marker
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(cellLocation).zoom(16).build();
                                neighbourCellsGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }
                        }
                        double[] phoneLatLong = getPhoneLatLong();
                        if (ConnectedCellFragment.connectedCellInfo.getLongitude() != -1) {
                            PolylineOptions line =
                                    new PolylineOptions().add(new LatLng(ConnectedCellFragment.connectedCellInfo.getLatitude(),
                                                    ConnectedCellFragment.connectedCellInfo.getLongitude()),
                                            new LatLng(phoneLatLong[0],
                                                    phoneLatLong[1]))
                                            .width(5).color(Color.RED);

                            neighbourCellsGoogleMap.addPolyline(line);
                        }
                    }
                });
            }
        }
    }

    private void getNeighbourCells() {
        double[] phoneLatLong = getPhoneLatLong();
        String sURL = String.format("http://2.84.143.223:4445/server/getcellinfoinarea/get/phoneLat/%f/phoneLong/%f", phoneLatLong[0], phoneLatLong[1]);

        URL url = null;
        try {
            url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();
            // Convert to a JSON object
            JsonParser jsonParser = new JsonParser(); //from gson
            JsonArray neighbourCellsJson = jsonParser.parse(new InputStreamReader((InputStream) request.getContent())).getAsJsonArray();
//            JsonArray neighbourCellsJson = root.getAsJsonObject().get("RadiusRing").getAsJsonObject().get("neighbourCells").getAsJsonArray();
            for (JsonElement neighbourCellJson : neighbourCellsJson) {
                JsonObject neighbourCellJsonObj = neighbourCellJson.getAsJsonObject();
                int cellId = neighbourCellJsonObj.get("cell").getAsInt();
                int lac = neighbourCellJsonObj.get("area").getAsInt();
                int tac = neighbourCellJsonObj.get("area").getAsInt();
                String rat = neighbourCellJsonObj.get("radio").getAsString();
                int mcc = neighbourCellJsonObj.get("mcc").getAsInt();
                int mnc = neighbourCellJsonObj.get("mcc").getAsInt();
                double latitude = neighbourCellJsonObj.get("lat").getAsDouble();
                double longitude = neighbourCellJsonObj.get("lon").getAsDouble();
                boolean connected = false;
                int rsrp = 0;
                int rssi = 0;
                int rsrq = 0;
                int rssnr = 0;
                int cqi = 0;
                if (ConnectedCellFragment.connectedCellInfo.getCellId() == cellId) {
                    rsrp = ConnectedCellFragment.connectedCellInfo.getRsrp();
                    rssi = ConnectedCellFragment.connectedCellInfo.getRssi();
                    rsrq = ConnectedCellFragment.connectedCellInfo.getRsrq();
                    rssnr = ConnectedCellFragment.connectedCellInfo.getRssnr();
                    cqi = ConnectedCellFragment.connectedCellInfo.getCqi();
                }
                neighbourCells.add(new _CellInfo(cellId, lac, tac, rssi, rsrp, rsrq, rssnr, cqi, rat, mcc, mnc, latitude, longitude, connected));
            }


        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public double[] getPhoneLatLong() {
        double[] latLong = new double[]{-1, -1};
        String serviceString = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(serviceString);
        android.location.LocationListener myLocationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latLong[0] = location.getLatitude();
            latLong[1] = location.getLongitude();

        }
        if (latLong[0] == -1) {
            location = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latLong[0] = location.getLatitude();
                latLong[1] = location.getLongitude();
            }
        }

        return latLong;
    }
}
