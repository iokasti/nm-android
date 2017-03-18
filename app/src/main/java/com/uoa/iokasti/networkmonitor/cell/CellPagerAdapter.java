package com.uoa.iokasti.networkmonitor.cell;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Locale;

class CellPagerAdapter extends FragmentStatePagerAdapter {
    Fragment[] screens;

    CellPagerAdapter(FragmentManager fm) {
        super(fm);
        screens = new Fragment[3];
        screens[0] = new ConnectedCellFragment();
        screens[1] = new NeighbourCellsFragment();
        screens[2] = new CellsMapFragment();
    }

    @Override
    public Fragment getItem(final int index) {
        if (index <= screens.length) {
            return screens[index];
        }
        return null;
    }

    @Override
    public int getCount() {
        return screens.length;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Cell Info";
            case 1:
                return "neighbour Cells";
            case 3:
                return "Cells Map";
        }
        return null;
    }

}
