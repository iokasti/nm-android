package com.uoa.iokasti.networkmonitor.cell;

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
                return new ConnectedCellFragment();
            case 1:
                return new NeighborCellsFragment();
            case 2:
                return new CellsMapFragment();

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
