package com.uoa.iokasti.networkmonitor.cell;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.uoa.iokasti.networkmonitor.R;
import com.uoa.iokasti.networkmonitor.entities._CellInfo;

import java.util.ArrayList;
import java.util.HashMap;


public class NeighbourCellsFragment extends Fragment {

    private ListView neighbourCellsListView;
    /* ListView Adapter Declaration */
    static ArrayAdapter<String> neighbourCellsListAdapter;

    static ArrayList<String> neighbourCellsIdList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_neighbour_cells, container, false);
        findViews(view);

        neighbourCellsIdList = new ArrayList<>();
        neighbourCellsListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, neighbourCellsIdList);
        neighbourCellsListView.setAdapter(neighbourCellsListAdapter);

        return view;
    }

    private void findViews(View view) {
        neighbourCellsListView = (ListView) view.findViewById(R.id.neighbour_cells_list);
    }

    @Override
    public void onResume() {
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
