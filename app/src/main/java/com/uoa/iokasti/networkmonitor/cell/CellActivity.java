package com.uoa.iokasti.networkmonitor.cell;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.uoa.iokasti.networkmonitor.R;

import java.util.Timer;

import static com.uoa.iokasti.networkmonitor.cell.ConnectedCellFragment.cellInfoScanInterval;
import static com.uoa.iokasti.networkmonitor.cell.ConnectedCellFragment.cellInfoScanTask;

public class CellActivity extends AppCompatActivity {

    ViewPager cell_pager;
    PagerTabStrip cell_tab_strip;

    Timer cellInfoScanTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell);
        CellPagerAdapter cellPagerAdapter = new CellPagerAdapter(getSupportFragmentManager());
        cell_pager = (ViewPager) findViewById(R.id.cell_pager);

        cell_pager.setAdapter(cellPagerAdapter);
        cell_tab_strip = (PagerTabStrip) findViewById(R.id.tab_strip);
        cell_tab_strip.setTextColor(Color.WHITE);

        cell_pager.setOffscreenPageLimit(1);

        // TODO fix this.
        final ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float
                    positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    cellInfoScanTimer = new Timer();
                    cellInfoScanTimer.scheduleAtFixedRate(cellInfoScanTask, 0, cellInfoScanInterval);
                } else if (position == 1) {
                    if (cellInfoScanTimer != null && cellInfoScanTask != null) {
                        cellInfoScanTimer.cancel();
                        cellInfoScanTimer.purge();
                        cellInfoScanTask.cancel();
                        cellInfoScanTimer = null;
                        cellInfoScanTask = null;
                    }
                } else if (position == 2) {
                    if (cellInfoScanTimer != null && cellInfoScanTask != null) {
                        cellInfoScanTimer.cancel();
                        cellInfoScanTimer.purge();
                        cellInfoScanTask.cancel();
                        cellInfoScanTimer = null;
                        cellInfoScanTask = null;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        cell_pager.addOnPageChangeListener(listener);
        cell_pager.setCurrentItem(0);
    }
}
