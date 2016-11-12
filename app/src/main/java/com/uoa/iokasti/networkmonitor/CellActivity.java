package com.uoa.iokasti.networkmonitor;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class CellActivity extends AppCompatActivity {

    ViewPager cell_pager;
    PagerTabStrip cell_tab_strip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell);
        CellPagerAdapter cellPagerAdapter = new CellPagerAdapter(getSupportFragmentManager());
        cell_pager = (ViewPager) findViewById(R.id.cell_pager);

        cell_pager.setAdapter(cellPagerAdapter);
        cell_tab_strip = (PagerTabStrip) findViewById(R.id.tab_strip);
        cell_tab_strip.setTextColor(Color.WHITE);
    }
}
