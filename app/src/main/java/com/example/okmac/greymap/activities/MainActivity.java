package com.example.okmac.greymap.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.okmac.greymap.R;
import com.example.okmac.greymap.adapters.CustomPagerAdapter;
import com.example.okmac.greymap.fragments.GeoTag;
import com.example.okmac.greymap.fragments.MapFragment;
import com.example.okmac.greymap.fragments.TagListFragment;

public class MainActivity extends AppCompatActivity {
    private MapFragment mapFragment;
    private TagListFragment tagListFragment;
    private ViewPager vpFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewPager();
    }

    private void initViewPager() {

        vpFragments = findViewById(R.id.vpFragments);
        CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());

        mapFragment = new MapFragment();
        tagListFragment = new TagListFragment();

        customPagerAdapter.addFragment(tagListFragment, getString(R.string.list_fragment_title));
        customPagerAdapter.addFragment(mapFragment, getString(R.string.map_fragment_title));

        TabLayout tlFragments = findViewById(R.id.tl_fragments);
        tlFragments.setupWithViewPager(vpFragments);

        vpFragments.setAdapter(customPagerAdapter);
    }

    public void notifyMapFragment(GeoTag geoTag) {
        vpFragments.setCurrentItem(0);
    }

    public void notifyDbUpdated() {
        vpFragments.setCurrentItem(0);
        tagListFragment.getTags();

    }
}