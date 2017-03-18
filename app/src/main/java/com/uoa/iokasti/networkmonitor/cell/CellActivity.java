package com.uoa.iokasti.networkmonitor.cell;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.uoa.iokasti.networkmonitor.R;

public class CellActivity extends AppCompatActivity {

    ViewPager cell_pager;
    PagerTabStrip cell_tab_strip;

    final CellPagerAdapter cellPagerAdapter = new CellPagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell);
        cell_pager = (ViewPager) findViewById(R.id.cell_pager);

        cell_pager.setAdapter(cellPagerAdapter);
        cell_tab_strip = (PagerTabStrip) findViewById(R.id.tab_strip);
        cell_tab_strip.setTextColor(Color.WHITE);

        cell_pager.setCurrentItem(0);
    }
}
