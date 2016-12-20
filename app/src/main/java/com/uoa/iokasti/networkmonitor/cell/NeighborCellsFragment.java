package com.uoa.iokasti.networkmonitor.cell;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uoa.iokasti.networkmonitor.R;
import com.uoa.iokasti.networkmonitor.entities.RadiusRing;
import com.uoa.iokasti.networkmonitor.entities._CellInfo;

import java.util.HashMap;


public class NeighborCellsFragment extends Fragment {


    //  private String openCellIdApiKey = "ef445193-fc82-482f-b199-9422b79a0e0a"; original
    private String openCellIdApiKey = "5712903e-fe28-4a66-a0c1-bd496178783f"; // temp

    private RadiusRing radiusRing;
    private int radiusInKm = 1;

    HashMap<Integer, _CellInfo> neighborCells;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_neighbor_cells, container, false);
        findViews(view);

        neighborCells = new HashMap<>();
        radiusRing = new RadiusRing();

        return view;
    }

    private void findViews(View view) {
    }

    @Override
    public void onResume() {
        Log.d("neighborccells onresume", "saywhat");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
