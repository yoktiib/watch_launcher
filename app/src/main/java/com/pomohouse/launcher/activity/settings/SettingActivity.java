package com.pomohouse.launcher.activity.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseActivity;
import com.pomohouse.launcher.fragment.settings.SettingFragment;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;

import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingActivity extends BaseActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (TCPSocketServiceProvider.getInstance() != null)
            TCPSocketServiceProvider.getInstance().checkSocketIsRunning();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initInstances(savedInstanceState, SettingFragment.newInstance());
    }

    private void initInstances(Bundle savedInstanceState, @NonNull Fragment main) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, main, main.getClass().getName()).commit();
        }
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }
}
