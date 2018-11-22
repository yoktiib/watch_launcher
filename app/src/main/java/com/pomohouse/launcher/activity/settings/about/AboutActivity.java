package com.pomohouse.launcher.activity.settings.about;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseActivity;
import com.pomohouse.component.viewpagerindicator.CirclePageIndicator;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class AboutActivity extends BaseActivity {
    MyPageAdapter adapter;
    ViewPager pager;
    CirclePageIndicator indicator;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        adapter = new MyPageAdapter(getSupportFragmentManager());
        pager =  findViewById(R.id.pager);
        pager.setAdapter(adapter);
        indicator = findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }
}
