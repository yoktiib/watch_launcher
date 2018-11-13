package com.pomohouse.launcher.activity.settings.network;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseActivity;
import com.pomohouse.launcher.manager.settings.ISettingManager;
import com.pomohouse.launcher.manager.settings.SettingPrefManager;
import com.pomohouse.launcher.manager.settings.SettingPrefModel;
import com.pomohouse.library.WearerInfoUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NetworkActivity extends BaseActivity {
    @BindView(R.id.ivOnOffDataNetwork)
    AppCompatImageView ivOnOffDataNetwork;
    @BindView(R.id.boxDataRoaming)
    RelativeLayout boxDataRoaming;
    @BindView(R.id.tvStatusNetwork)
    TextView tvStatusNetwork;
    private boolean isDataNetwork = false;
    ISettingManager settingManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netwok);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        WearerInfoUtils wearerInfoUtils = WearerInfoUtils.getInstance().initWearerInfoUtils(this);
        settingManager = new SettingPrefManager(this);
        if (wearerInfoUtils.isHaveSimCard()) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                ivOnOffDataNetwork.setImageResource(R.drawable.network_on);
                tvStatusNetwork.setTextColor(getResources().getColor(R.color.waffle_orange_01_color));
                tvStatusNetwork.setText(getString(R.string.network_on));
                isDataNetwork = true;
            } else {
                tvStatusNetwork.setTextColor(getResources().getColor(R.color.waffle_grey_03_color));
                tvStatusNetwork.setText(getString(R.string.network_off));
                ivOnOffDataNetwork.setImageResource(R.drawable.network_off);
                isDataNetwork = false;
            }
        } else {
            tvStatusNetwork.setText(getString(R.string.network_no_sim));
            tvStatusNetwork.setTextColor(getResources().getColor(R.color.waffle_pink_02_color));
            ivOnOffDataNetwork.setImageResource(R.drawable.network_off);
            boxDataRoaming.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.boxHeader)
    public void boxHeaderClick(View view) {
        dataNetworkState();
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }

    @OnClick(R.id.ivOnOffDataNetwork)
    public void onClickDataNetwork() {
        dataNetworkState();
    }

    public void dataNetworkState() {
        if (!WearerInfoUtils.getInstance().isHaveSimCard())
            return;
        if (isDataNetwork) {
            tvStatusNetwork.setTextColor(getResources().getColor(R.color.waffle_grey_03_color));
            ivOnOffDataNetwork.setImageResource(R.drawable.network_off);
            tvStatusNetwork.setText(getString(R.string.network_off));
            onSetupMobileData(false);
        } else {
            tvStatusNetwork.setTextColor(getResources().getColor(R.color.waffle_orange_01_color));
            ivOnOffDataNetwork.setImageResource(R.drawable.network_on);
            tvStatusNetwork.setText(getString(R.string.network_on));
            onSetupMobileData(true);
        }
    }

    /*
    @OnClick(R.id.ivOnOffDataRoaming)
    public void onClickDataRoaming() {
        if (isDataRoaming) {
            ivOnOffDataRoaming.setImageResource(R.drawable.network_off);
            onSetupDataRoaming(false);
        } else {
            ivOnOffDataRoaming.setImageResource(R.drawable.network_on);
            onSetupDataRoaming(true);
        }
    }
    */

    public void onSetupMobileData(boolean stateOn) {
        isDataNetwork = stateOn;
        Intent dataOnIntent = new Intent("com.pomohouse.waffle.REQUEST_MOBILE_DATA");
        dataOnIntent.putExtra("status", stateOn ? "on" : "off");
        sendBroadcast(dataOnIntent);
        if (settingManager != null) {
            SettingPrefModel settingPrefModel = settingManager.getSetting();
            settingPrefModel.setMobileData(stateOn);
            settingManager.addMiniSetting(settingPrefModel);
        }
    }
/*
    public void onSetupDataRoaming(boolean stateOn) {
        Intent dataRoamingOnIntent = new Intent("com.pomohouse.waffle.REQUEST_DATA_ROAMING");
        dataRoamingOnIntent.putExtra("status", stateOn ? "on" : "off");
        sendBroadcast(dataRoamingOnIntent);
    }

    public boolean getMobileDataState(Context context) {
        TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method getDataEnabled = telephonyService.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getDataEnabled) {
                return (Boolean) getDataEnabled.invoke(telephonyService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }*/
}
