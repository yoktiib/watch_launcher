package com.pomohouse.launcher.activity.settings.options;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseActivity;
import com.pomohouse.launcher.fragment.mini_setting.MiniSettingFragment;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OptionSettingActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_setting);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        int menuID = getIntent().getExtras().getInt(MiniSettingFragment.MENU_SELECTED, 0);
        initInstances(savedInstanceState, MiniSettingFragment.newInstance(menuID));
    }

    private void initInstances(Bundle savedInstanceState, @NonNull Fragment main) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, main, main.getClass().getName())
                    .commit();
        }
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }
}