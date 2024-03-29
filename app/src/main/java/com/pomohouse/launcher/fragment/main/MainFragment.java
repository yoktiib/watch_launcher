package com.pomohouse.launcher.fragment.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.activity.getstarted.GetStartActivity;
import com.pomohouse.launcher.activity.theme.ThemeActivity;
import com.pomohouse.launcher.activity.theme.ThemeType;
import com.pomohouse.launcher.base.ActivityResultCode;
import com.pomohouse.launcher.base.BaseFragment;
import com.pomohouse.launcher.base.BaseThemeFragment;
import com.pomohouse.launcher.broadcast.receivers.DeviceActionReceiver;
import com.pomohouse.launcher.broadcast.receivers.EventReceiver;
import com.pomohouse.launcher.di.module.MainFragmentPresenterModule;
import com.pomohouse.launcher.fragment.main.presenter.IMainFragmentPresenter;
import com.pomohouse.launcher.fragment.theme.ThemeAnalogFragment;
import com.pomohouse.launcher.fragment.theme.ThemeDigitalFragment;
import com.pomohouse.launcher.manager.settings.ISettingManager;
import com.pomohouse.launcher.manager.theme.IThemePrefManager;
import com.pomohouse.launcher.manager.theme.ThemePrefModel;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.launcher.utils.CombineObjectConstance;
import com.pomohouse.launcher.utils.SoundPoolManager;
import com.pomohouse.launcher.utils.TelephoneState;
import com.pomohouse.launcher.utils.VibrateManager;
import com.pomohouse.launcher.utils.callbacks.SignalInfoListener;
import com.pomohouse.library.WearerInfoUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnLongClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_INTERNET_AVAILABLE;
import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_INTERNET_UN_AVAILABLE;


/**
 * Fragment to manage the central page of the 5 pages application navigation (top, center, bottom, left, right).
 */
public class MainFragment extends BaseFragment implements IMainFragmentView {
    private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    private BaseThemeFragment currentTheme;
    private ArrayList<ThemePrefModel> themePrefModelArrayList;
    private TelephoneState telephoneState;
    private SoundPoolManager soundPoolManager;
    private VibrateManager vibrateManager;
    private Context context;

    private Disposable networkDisposable;
    private Disposable internetDisposable;

    @BindView(R.id.ivBattery)
    ImageView ivBattery;
    @BindView(R.id.ivSignal)
    ImageView ivSignal;
    @BindView(R.id.ivInternetType)
    ImageView ivInternetType;
    @BindView(R.id.tvBattery)
    TextView tvBattery;
    @Inject
    IThemePrefManager themeManager;
    @Inject
    IMainFragmentPresenter presenter;
    @Inject
    ISettingManager settingManager;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        telephoneState = new TelephoneState(getContext());
        soundPoolManager = SoundPoolManager.getInstance(getContext());
        vibrateManager = VibrateManager.getInstance(getContext());
    }

    public MainFragment() {
        setRetainInstance(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.onInitial(getActivity());
        try {
            if (telephoneState == null) telephoneState = new TelephoneState(getContext());
            telephoneState.init(signalListener);
            if (themePrefModelArrayList == null)
                themePrefModelArrayList = themeManager.getDataTheme();
            ivInternetType.setVisibility(View.GONE);
            presenter.onBatteryLevelInfoAndResource(getContext());
            this.onThemeInitial();
            /**
             * Init Device and Event Receiver.
             */
            EventReceiver.getInstance().initEventMainListener(this::onEventReceived);
            DeviceActionReceiver.getInstance().initDeviceActionListener(this::onDeviceStatusActionReceived);
        } catch (Exception ignore) {
        }
    }

    SignalInfoListener signalListener = new SignalInfoListener() {
        @Override
        public void onSignalChanged(int signal) {
            presenter.onSignalChange(signal);
        }

        @Override
        public void onNoSimCard() {
            presenter.onNoSimCardPlugin();
            // onCheckGetStart();
        }

        @Override
        public void onSimCardReady() {
            if (settingManager != null && settingManager.getSetting().isMobileData()) {
                Intent dataOnIntent = new Intent("com.pomohouse.waffle.REQUEST_MOBILE_DATA");
                dataOnIntent.putExtra("status", "on");
                if (getActivity() != null) getActivity().sendBroadcast(dataOnIntent);
            }
        }
    };

    public void onCheckGetStart() {
        WearerInfoUtils wearerInfoUtils = WearerInfoUtils.getInstance().initWearerInfoUtils(getActivity());
        if (getActivity() != null && settingManager != null && settingManager.getSetting().isFirstTime()) {
            if (wearerInfoUtils.isHaveSimCard() || (settingManager.getSetting().isFirstTimeNoSim() && !wearerInfoUtils.isHaveSimCard()))
                startActivity(new Intent(getActivity(), GetStartActivity.class));
        }
    }

    @OnLongClick(R.id.container)
    boolean onThemeChange() {
        Intent intent = new Intent(this.getActivity(), ThemeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, ActivityResultCode.RESULT_CODE_CHANGE_THEME);
        return true;
    }

    private void onEventReceived(EventDataInfo eventDataInfo) {
        presenter.updateEventReceiver(eventDataInfo);
    }

    private void onDeviceStatusActionReceived(Intent intent) {
        presenter.onDeviceStatusActionReceived(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            checkThemeChange();
            presenter.onBatteryLevelInfo(getContext());
            context.registerReceiver(simStateReceiver, new IntentFilter(ACTION_SIM_STATE_CHANGED));

            networkDisposable = ReactiveNetwork.observeNetworkConnectivity(getActivity()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(connectivity -> {
                Log.d(TAG, connectivity.toString());
                final NetworkInfo.State state = connectivity.state();
                // final String name = connectivity.typeName();
                //tvConnectivityStatus.setText(String.format("state: %s, typeName: %s", state, name));

                if (connectivity.typeName().equalsIgnoreCase("WIFI")) {
                    ivInternetType.setVisibility(View.VISIBLE);
                    Timber.e("connectedWiFiAvailable");
                    setImageFromResource(R.drawable.signal_wifi_network, ivInternetType);
                    sendInternetAvailable(true);
                } else if (connectivity.typeName().equalsIgnoreCase("MOBILE")) {
                    ivInternetType.setVisibility(View.VISIBLE);
                    Timber.e("connectedMobileNetwork");
                    setImageFromResource(R.drawable.signal_3g_network, ivInternetType);
                    sendInternetAvailable(true);
                } else {
                    Timber.e("Network - Connection GONE");
                    ivInternetType.setVisibility(View.GONE);
                    sendInternetAvailable(false);
                }

            });
            internetDisposable = ReactiveNetwork.observeInternetConnectivity().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(isConnected -> {

            });
        } catch (Exception ignored) {

        }
    }

    private static final String TAG = "ReactiveNetwork";

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(simStateReceiver);
        safelyDispose(networkDisposable, internetDisposable);
    }

    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }

    private BroadcastReceiver simStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (telephoneState == null) telephoneState = new TelephoneState(getContext());
                telephoneState.init(signalListener);
            } catch (Exception ignore) {
            }
        }
    };

    private void onThemeInitial() {
        if (themeManager.getCurrentTheme().getPosition() == 0 && themePrefModelArrayList.size() > 0)
            themeManager.addCurrentTheme(themePrefModelArrayList.get(0));
        if (themeManager.getCurrentTheme().getThemeType() == ThemeType.DIGITAL)
            replaceFragment(ThemeDigitalFragment.newInstance(themeManager.getCurrentTheme()));
        else replaceFragment(ThemeAnalogFragment.newInstance(themeManager.getCurrentTheme()));
    }

    public void checkThemeChange() {
//  void checkThemeChange() {
        Timber.e(themeManager.getCurrentTheme().isChanged() + " : " + themeManager.getCurrentTheme().getThemeId());
        if (themeManager != null && themeManager.getCurrentTheme().isChanged()) {
            themeManager.addCurrentTheme(themeManager.getCurrentTheme().setChanged(false));
            onThemeInitial();
        }
    }

    protected void replaceFragment(BaseThemeFragment fragment) {
        if (fragment != null) {
            currentTheme = fragment;
            getChildFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
        }
    }

    @Override
    protected List<Object> injectModules() {
        return Collections.singletonList(new MainFragmentPresenterModule(this));
    }

    @Override
    public void onBatteryChanged(int battery, long level) {
        try {
            if (getActivity() != null) {
                if (CombineObjectConstance.getInstance().isBatteryStatusCharging())
                    ivBattery.setImageResource(R.drawable.battery_charging);
                else ivBattery.setImageResource(battery);
                tvBattery.setText(String.format(getString(R.string.battery_format), level));
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onBatteryChanged(long level) {
        try {
            if (getActivity() != null) {
                tvBattery.setText(String.format(getString(R.string.battery_format), level));
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onSignalChanged(int drawable) {
        try {
            ivSignal.setImageResource(drawable);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void powerConnected() {
        try {
            if (getActivity() != null) ivBattery.setImageResource(R.drawable.battery_charging);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void powerUnConnected() {
        presenter.onBatteryLevelInfoAndResource(getContext());
    }

    @Override
    public void batteryFully() {
        try {
            if (getActivity() != null)
                tvBattery.setText(String.format(getString(R.string.battery_format), 100));
        } catch (Exception ignored) {
        }
    }

    @Override
    public void batteryOkay(Intent intent) {

    }

    @Override
    public void batteryLow(Intent intent) {
    }

    @Override
    public void onGroupChatEventReceived(EventDataInfo eventData) {
        this.onPlayMessagePlayer();
        if (currentTheme != null && eventData != null) currentTheme.eventReceived(eventData);
    }

    @Override
    public void onMessageEventReceived(EventDataInfo eventData) {
        this.onPlayMessagePlayer();
        if (currentTheme != null && eventData != null && getActivity() != null)
            currentTheme.eventReceived(eventData);
    }

    public void onPlayMessagePlayer() {
        if (vibrateManager != null) vibrateManager.notificationVibration();
        if (soundPoolManager != null) soundPoolManager.playNotification();
    }

    @Override
    public void onNotificationChanged(EventDataInfo eventData) {
        if (currentTheme != null && eventData != null && getActivity() != null)
            currentTheme.eventReceived(eventData);
    }

    @Override
    public void onTimeZoneChange(EventDataInfo eventData) {
        if (currentTheme != null && eventData != null && getActivity() != null)
            currentTheme.eventReceived(eventData);
    }

    private void sendInternetAvailable(boolean inAvailable) {
        Intent intent;
        if (inAvailable) intent = new Intent(SEND_EVENT_INTERNET_AVAILABLE);
        else intent = new Intent(SEND_EVENT_INTERNET_UN_AVAILABLE);
        getContext().sendBroadcast(intent);
    }

    public void setImageFromResource(int internetRes, ImageView ivAvatar) {
        try {
            ivAvatar.setImageResource(internetRes);
        } catch (Exception ignore) {
            ivAvatar.setImageResource(R.drawable.ic_launcher);
        }
    }
}
