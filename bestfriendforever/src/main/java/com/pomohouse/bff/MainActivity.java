package com.pomohouse.bff;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.pomohouse.bff.base.BaseActivity;
import com.pomohouse.bff.dao.WearerInfo;
import com.pomohouse.bff.di.presenter.CancelFriendForeverPresenterModule;
import com.pomohouse.bff.di.presenter.MainPresenterModule;
import com.pomohouse.bff.fragment.BestFriendForeverFragment;
import com.pomohouse.bff.fragment.ICancelView;
import com.pomohouse.bff.fragment.presenter.IClosePresenter;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.manager.ActivityContextor;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends BaseActivity implements ICancelView {

    @Inject
    IClosePresenter presenterCancel;
    //private WearerInfo wearerInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityContextor.getInstance().init(this);
        WearerInfoUtils.getInstance().initWearerInfoUtils(this);
        presenterCancel.onInitial(new WearerInfo().setImei(WearerInfoUtils.getInstance().getImei()));
        LocationBroadcast.getInstance(this).startLocationService().initLocationListener(location -> Toast.makeText(MainActivity.this, "Alert Location : " + location.getIp() + " : " + location.getLatitude() + " : " + location.getLongitude(), Toast.LENGTH_SHORT).show());
        setTimeout(60);
        KillAppReceiver.getInstance().startEventReceiver(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) return;
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        initInstances(savedInstanceState, BestFriendForeverFragment.newInstance());
    }

    private void initInstances(Bundle savedInstanceState, @NonNull Fragment main) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, main, main.getClass().getName()).commit();
        }
    }

    private void setTimeout(int screenOffTimeout) {
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, screenOffTimeout * 10000);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
        Timber.e("onPause");
        presenterCancel.requestCancelRequestFriend();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.e("onStop");
        presenterCancel.requestCancelRequestFriend();
    }

    @Override
    protected List<Object> getModules() {
        return Arrays.asList(new MainPresenterModule(), new CancelFriendForeverPresenterModule(this));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void successCancel() {
        Process.killProcess(Process.myPid());
    }

    @Override
    public void failureCancel() {
        Process.killProcess(Process.myPid());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}