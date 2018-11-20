package com.pomohouse.launcher.fragment.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.activity.settings.about.AboutActivity;
import com.pomohouse.launcher.activity.settings.network.NetworkActivity;
import com.pomohouse.launcher.activity.settings.options.OptionSettingActivity;
import com.pomohouse.launcher.activity.theme.ThemeActivity;
import com.pomohouse.launcher.base.BaseFragment;
import com.pomohouse.launcher.di.module.SettingPresenterModule;
import com.pomohouse.launcher.dialog.ConfirmDialogFragment;
import com.pomohouse.launcher.fragment.mini_setting.MiniSettingFragment;
import com.pomohouse.launcher.fragment.settings.presenter.ISettingPresenter;
import com.pomohouse.launcher.tcp.CMDCode;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Fragment to manage the top page of the 5 pages application navigation (top, center, bottom, left, right).
 */
public class SettingFragment extends BaseFragment implements ISettingView {

    @Inject
    ISettingPresenter presenter;
    @BindView(R.id.rcvSetting)
    RecyclerView rcvSetting;


    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.onInitial();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rcvSetting.setLayoutManager(mLayoutManager);
        rcvSetting.setItemAnimator(new DefaultItemAnimator());
        rcvSetting.setAdapter(new SettingAdapter(createSettingMenu(), (view1, position, isLongClick) -> {
            Bundle bundle;
            try {
                switch (position) {
                    case 0:
                        openActivity(AboutActivity.class);
                        break;
                    case 1:
                        bundle = new Bundle();
                        bundle.putInt(MiniSettingFragment.MENU_SELECTED, 1);
                        openActivity(OptionSettingActivity.class, bundle);
                        break;
                    case 2:
                        bundle = new Bundle();
                        bundle.putInt(MiniSettingFragment.MENU_SELECTED, 0);
                        openActivity(OptionSettingActivity.class, bundle);
                        break;
                    case 3:
                        openActivity(ThemeActivity.class);
                        break;
                    /*case 4:
                        Intent intentBT = new Intent("com.gt.watchsettings.BLUETOOTH_SETTINGS");
                        startActivity(intentBT);
                        break;*/
//                    case 5:
//                        openActivity(NetworkActivity.class);
//                        break;
                    case 4:
                        Intent intentWIFI = new Intent("com.gt.watchsettings.WIFI_SETTINGS");
                        startActivity(intentWIFI);
                        break;
                    case 5:
                        showAlertDialogFragment(ConfirmDialogFragment.newInstance(restart, ConfirmDialogFragment._KEY_RESTART), ConfirmDialogFragment.class.getName());
                        break;
                    case 6:
                        showAlertDialogFragment(ConfirmDialogFragment.newInstance(shutdown, ConfirmDialogFragment._KEY_POWER_OFF), ConfirmDialogFragment.class.getName());
                        break;
                    case 7:
                        ComponentName componentName = new ComponentName("com.rock.gota", "com.rock.gota.MainActivity");
                        Intent updateIntent = new Intent();
                        updateIntent.setComponent(componentName);
                        startActivity(updateIntent);
                        break;
                }
            } catch (Exception ignored) {
            }/*

            try {
                switch (position) {
                    case 0:
                        bundle = new Bundle();
                        bundle.putInt(MiniSettingFragment.MENU_SELECTED, 1);
                        openActivity(OptionSettingActivity.class, bundle);
                        break;
                    case 1:
                        bundle = new Bundle();
                        bundle.putInt(MiniSettingFragment.MENU_SELECTED, 0);
                        openActivity(OptionSettingActivity.class, bundle);
                        break;
                    case 2:
                        openActivity(ThemeActivity.class);
                        break;
//                    case 4:
//                        Intent intentBT = new Intent("com.gt.watchsettings.BLUETOOTH_SETTINGS");
//                        startActivity(intentBT);
//                        break;
//                    case 5:
//                        openActivity(NetworkActivity.class);
//                        break;
                    case 3:
                        Intent intentWIFI = new Intent("com.gt.watchsettings.WIFI_SETTINGS");
                        startActivity(intentWIFI);
                        break;
                    case 4:
                        showAlertDialogFragment(ConfirmDialogFragment.newInstance(restart, ConfirmDialogFragment._KEY_RESTART), ConfirmDialogFragment.class.getName());
                        break;
                    case 5:
                        showAlertDialogFragment(ConfirmDialogFragment.newInstance(shutdown, ConfirmDialogFragment._KEY_POWER_OFF), ConfirmDialogFragment.class.getName());
                        break;
                    case 6:
                        ComponentName componentName = new ComponentName("com.rock.gota", "com.rock.gota.MainActivity");
                        Intent updateIntent = new Intent();
                        updateIntent.setComponent(componentName);
                        startActivity(updateIntent);
                        break;
                }
            } catch (Exception ignored) {
            }*/
        }));

    }

    private void shutDown() {

        try {
            //获得ServiceManager类
            Log.e("zglkey", "----------shutdown--1");
            Class<?> ServiceManager = Class
                    .forName("android.os.ServiceManager");
            //获得ServiceManager的getService方法
            Method getService = ServiceManager.getMethod("getService", java.lang.String.class);
            //调用getService获取RemoteService
            Object oRemoteService = getService.invoke(null, Context.POWER_SERVICE);
            //获得IPowerManager.Stub类
            Class<?> cStub = Class
                    .forName("android.os.IPowerManager$Stub");
            //获得asInterface方法
            Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);
            //调用asInterface方法获取IPowerManager对象
            Object oIPowerManager = asInterface.invoke(null, oRemoteService);
            //获得shutdown()方法
            Method shutdown = oIPowerManager.getClass().getMethod("shutdown", boolean.class, boolean.class);
            //调用shutdown()方法
            shutdown.invoke(oIPowerManager, false, true);
            //Log.e("zglkey", "----------shutdown--2");
        } catch (Exception e) {
            Log.e("zglkey", e.toString(), e);
        }

    }

    private void reStartPhone() {
        Log.e("zglkey", "----------reStartPhone--");
        PowerManager pManager = (PowerManager) Objects.requireNonNull(getActivity()).getSystemService(Context.POWER_SERVICE);
        pManager.reboot("");
    }


    private void reStartPhone2(){
        Intent intent = new Intent(Intent.ACTION_REBOOT);
        intent.putExtra("nowait", 1);
        intent.putExtra("interval", 1);
        intent.putExtra("window", 0);
        Objects.requireNonNull(getActivity()).sendBroadcast(intent);
    }

    final ConfirmDialogFragment.ConfirmDialogListener restart = new ConfirmDialogFragment.ConfirmDialogListener() {
        @Override
        public void onYesDialogClick() {
            TCPSocketServiceProvider.getInstance().sendMessage(CMDCode.CMD_RESTART, "{}");
            reStartPhone();
        }

        @Override
        public void onNoDialogClick() {
        }
    };

    final ConfirmDialogFragment.ConfirmDialogListener shutdown = new ConfirmDialogFragment.ConfirmDialogListener() {
        @Override
        public void onYesDialogClick() {
            TCPSocketServiceProvider.getInstance().sendMessage(CMDCode.CMD_SHUTDOWN, "{}");
            shutDown();
        }

        @Override
        public void onNoDialogClick() {

        }
    };


    public ArrayList<SettingMenuData> createSettingMenu() {
        Timber.e("createSettingMenu");
        String[] settingMenuNameArr = getResources().getStringArray(R.array.settingMenuNameArr);
        TypedArray tArray = getResources().obtainTypedArray(R.array.settingMenuDrawableArr);
        int count = tArray.length();
        int[] settingMenuDrawableArr = new int[count];
        for (int i = 0; i < settingMenuDrawableArr.length; i++) {
            settingMenuDrawableArr[i] = tArray.getResourceId(i, 0);
        }
        tArray.recycle();

        ArrayList<SettingMenuData> listMenu = new ArrayList<>();
        for (int i = 0; i < settingMenuNameArr.length; i++) {
            SettingMenuData item = new SettingMenuData();
            item.setIcon(settingMenuDrawableArr[i]);
            item.setName(settingMenuNameArr[i]);
            listMenu.add(item);
        }
        return listMenu;
    }

    @Override
    protected List<Object> injectModules() {
        return Collections.singletonList(new SettingPresenterModule(this));
    }
}
