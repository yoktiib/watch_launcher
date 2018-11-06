package com.pomohouse.launcher.activity.getstarted;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.pomohouse.component.viewpagerindicator.CirclePageIndicator;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseActivity;
import com.pomohouse.launcher.manager.settings.SettingPrefManager;
import com.pomohouse.launcher.manager.settings.SettingPrefModel;
import com.pomohouse.library.WearerInfoUtils;

import java.util.List;

import butterknife.BindView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GetStartActivity extends BaseActivity {
    GetStartPageAdapter adapter;
    ViewPager pager;
    CirclePageIndicator indicator;
    @BindView(R.id.containerNoSim)
    RelativeLayout containerNoSim;
    @BindView(R.id.containerHaveSim)
    RelativeLayout containerHaveSim;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_start);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        WearerInfoUtils wearerInfoUtils = WearerInfoUtils.getInstance();

        SettingPrefManager settingPrefManager = new SettingPrefManager(this);
        SettingPrefModel settingPrefModel = settingPrefManager.getSetting();
        if (settingPrefModel != null) {
            settingPrefModel.setFirstTime(true);
            settingPrefManager.addMiniSetting(settingPrefModel);
        }

        if (settingPrefModel != null) {
            settingPrefModel.setFirstTime(true);
            settingPrefManager.addMiniSetting(settingPrefModel);
        }

        if (wearerInfoUtils.isHaveSimCard()) {
            if (settingPrefModel != null)
                settingPrefModel.setFirstTime(false);
            containerHaveSim.setVisibility(View.VISIBLE);
            adapter = new GetStartPageAdapter(getSupportFragmentManager());
            pager = (ViewPager) findViewById(R.id.pager);
            pager.setAdapter(adapter);
            indicator = (CirclePageIndicator) findViewById(R.id.indicator);
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
        } else {
            if (settingPrefModel != null)
                settingPrefModel.setFirstTimeNoSim(false);
            containerNoSim.setVisibility(View.VISIBLE);
        }
        settingPrefManager.addMiniSetting(settingPrefModel);
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }
}
