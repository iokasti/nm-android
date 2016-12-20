package com.uoa.iokasti.networkmonitor.cell;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.uoa.iokasti.networkmonitor.R;
import com.uoa.iokasti.networkmonitor.entities.RadiusRing;
import com.uoa.iokasti.networkmonitor.entities._CellInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class CellsMapFragment extends Fragment {

    //  private String openCellIdApiKey = "ef445193-fc82-482f-b199-9422b79a0e0a"; original
    private String openCellIdApiKey = "5712903e-fe28-4a66-a0c1-bd496178783f"; // temp

    private MapView cellsMapView;
    private GoogleMap cellsGoogleMap;

    private RadiusRing radiusRing;
    private int radiusInKm = 1;

    HashMap<Integer, _CellInfo> neighborCells;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cells_map, container, false);
        findViews(view);

        /* map initialization */
        cellsMapView.onCreate(savedInstanceState);
        cellsMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

//        connectedCellInfo = new _CellInfo();

        neighborCells = new HashMap<>();
        radiusRing = new RadiusRing();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        new cellsInfoScan().execute();

        return view;
    }

    private void findViews(View view) {
        cellsMapView = (MapView) view.findViewById(R.id.cells_mapview);
    }

    @Override
    public void onResume() {
        super.onResume();
        cellsMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        cellsMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cellsMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        cellsMapView.onLowMemory();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class cellsInfoScan extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isDetached()) {
                cellsMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        cellsGoogleMap = mMap;

                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        cellsGoogleMap.setMyLocationEnabled(true);
                        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
                        Criteria criteria = new Criteria();

                        Location location = locationManager.getLastKnownLocation(locationManager
                                .getBestProvider(criteria, false));
                        radiusRing.setPhoneLat(location.getLatitude());
                        radiusRing.setPhoneLong(location.getLongitude());
                        radiusRing.calculateRing(radiusInKm);
                        getCellsInRadius(radiusRing);
                    }
                });
            }
        }
    }

    private ArrayList<_CellInfo> getCellsInRadius(RadiusRing radiusRing) {
        String sURL = String.format(Locale.US, "http://opencellid.org/cell/getInArea?key=%s&BBOX=%f,%f,%f,%f,&mcc=%d&mnc=%d&lac=%d&limit=10&format=json",
                openCellIdApiKey, radiusRing.getMinLat(), radiusRing.getMinLong(), radiusRing.getMaxLat(), radiusRing.getMaxLong(), 202, 1, 4037);
        Log.d("sUrl", sURL);
        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
//            request.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                stringBuilder.append(inputLine + "\n");
            in.close();

            // Convert to a JSON object
            JsonParser jsonParser = new JsonParser(); //from gson
            JsonElement root = jsonParser.parse(stringBuilder.toString());
            JsonElement cellsJson = root.getAsJsonObject().get("cells");
            JSONArray cellsJsonArray = new JSONArray(cellsJson.toString());
            for(int i=0; i<cellsJsonArray.length(); i++){
                JSONObject cell = cellsJsonArray.getJSONObject(i);
                Log.d("cell", cell.toString());
                //TODO add cells to neighborCells map
//                neighborCells.put(cell.getInt("cellid"),
//                        new _CellInfo(cell.getInt("cellid"),cell.getInt("lac"), cell.getInt("lac"), cell.getInt()));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
