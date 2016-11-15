package com.uoa.iokasti.networkmonitor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

public class CellPagerAdapter extends FragmentPagerAdapter {
    public CellPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                CellInfoFragment cellInfoFragment = new CellInfoFragment();
                return cellInfoFragment;
            case 1:
                NeighborCellsFragment neighborCellsFragment = new NeighborCellsFragment();
                return neighborCellsFragment;
            case 2:
                CellsMapFragment cellsMapFragment = new CellsMapFragment();
                return cellsMapFragment;

        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }//set the number of tabs

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return "Cell Info";
            case 1:
                return "Neighbor Cells";
            case 2:

                return "Cells Map";
        }
        return null;
    }

}
