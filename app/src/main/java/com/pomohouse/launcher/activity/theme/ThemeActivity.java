package com.pomohouse.launcher.activity.theme;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseActivity;
import com.pomohouse.launcher.fragment.theme.ChooseThemeFragment;
import com.pomohouse.launcher.main.presenter.LauncherPresenterImpl;
import com.pomohouse.launcher.manager.theme.IThemePrefManager;
import com.pomohouse.launcher.manager.theme.ThemePrefManager;
import com.pomohouse.launcher.manager.theme.ThemePrefModel;

import java.util.ArrayList;
import java.util.List;

public class ThemeActivity extends BaseActivity {
    private ArrayList<ThemePrefModel> themePrefModelArrayList;
    private static int NUM_PAGES = 1;
    IThemePrefManager iBackgroundManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        iBackgroundManager = new ThemePrefManager(this);
        themePrefModelArrayList = iBackgroundManager.getDataTheme();
        if(isRilakkumaThemeOpen()){
            NUM_PAGES = themePrefModelArrayList.size();
        }else{
            NUM_PAGES = themePrefModelArrayList.size() - 3;
        }

    }

    private boolean isRilakkumaThemeOpen(){
        return this.getSharedPreferences(LauncherPresenterImpl.THEME_SETTING, Activity.MODE_PRIVATE)
                .getInt(LauncherPresenterImpl.RILAKKUMA_THEME_STATUS, 0) != 0;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        createViewPager();
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }

    private void createViewPager() {
        int idx = 0;
        if (iBackgroundManager.getCurrentTheme() != null)
            idx = iBackgroundManager.getCurrentTheme().getPosition() - 1;
        ViewPager mPager = (ViewPager) findViewById(R.id.pagerBackground);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(idx < 0 ? 0 : idx);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ChooseThemeFragment.newInstance(themePrefModelArrayList.get(position));
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
