package com.pomohouse.launcher.fragment.main.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.CellSignalStrength;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.fragment.main.IMainFragmentView;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.launcher.utils.CombineObjectConstance;
import com.pomohouse.library.base.BaseRetrofitPresenter;

import org.json.JSONObject;

import timber.log.Timber;

import static android.content.Context.BATTERY_SERVICE;
import static com.pomohouse.launcher.utils.EventConstant.EventChat.EVENT_GROUP_CHAT_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventChat.EVENT_MESSAGE_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventDeviceInfo.EVENT_APP_TIMEZONE_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventDeviceInfo.EVENT_TIMEFORMAT_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventSetting.EVENT_NOTIFICATION_CALL_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventSetting.EVENT_NOTIFICATION_MESSAGE_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventSetting.EVENT_NOTIFICATION_VOLUME_CODE;


/**
 * Created by Admin on 8/19/16 AD.
 */
public class MainFragmentPresenterImpl extends BaseRetrofitPresenter implements IMainFragmentPresenter {
    private static final int BATTERY_MESSAGE = 1;
    private static final int BATTERY_PERCENT_MESSAGE = 5;
    private static final int SIGNAL_MESSAGE = 2;
    private static final int NO_SIM_MESSAGE = 3;
    private static final int EVENT_MESSAGE = 4;
    private IMainFragmentView view;
    //private IMainFragmentInteractor interactor;
    private Context mContext;

    public MainFragmentPresenterImpl(IMainFragmentView view) {
        this.view = view;
        //this.interactor = interactor;
    }

    @Override
    public void onInitial(Object... param) {
        super.onInitial(param);
        try {
            if (param == null)
                return;
            if (param.length < 1)
                return;
            if (param[0] == null)
                return;
            if (param[0] instanceof Context) {
                mContext = (Context) param[0];
            }
        } catch (Exception ignore) {
        }
    }
/*
    @Override
    public void checkEventUnreadAndVolume(EventDataInfo _eventDataInfo) {
        if (_eventDataInfo == null)
            return;
        _handler.sendMessage(_handler.obtainMessage(EVENT_MESSAGE, _eventDataInfo));
    }*/

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onBatteryLevelInfo(Context mContext) {
        _handler.sendMessage(_handler.obtainMessage(BATTERY_PERCENT_MESSAGE, getBatterySystem()));
        if (CombineObjectConstance.getInstance().isBatteryStatusCharging())
            view.powerConnected();
    }

    @Override
    public void onBatteryLevelInfoAndResource(Context mContext) {
        _handler.sendMessage(_handler.obtainMessage(BATTERY_MESSAGE, getBatterySystem()));
    }

    @Override
    public void updateEventReceiver(EventDataInfo eventData) {
        if (eventData == null)
            return;
        _handler.sendMessage(_handler.obtainMessage(EVENT_MESSAGE, eventData));
    }

    private final Handler _handler = new Handler(msg -> {
        try {
            switch (msg.what) {
                case BATTERY_PERCENT_MESSAGE:
                    int batteryPercent = (int) msg.obj;
                    view.onBatteryChanged(batteryPercent);
                    break;
                case BATTERY_MESSAGE:
                    int battery = (int) msg.obj;
                    view.onBatteryChanged(batteryConvertToResource(battery), battery);
                    break;
                case SIGNAL_MESSAGE:
                    int signal = (Integer) msg.obj;
                    view.onSignalChanged(signalConvertToResource(signal));
                    break;
                case NO_SIM_MESSAGE:
                    view.onSignalChanged(R.drawable.signal_no_sim);
                    break;
                case EVENT_MESSAGE:
                    EventDataInfo _lastEvent = (EventDataInfo) msg.obj;
                    if (_lastEvent == null)
                        return true;
                    notifyEventReceiver(_lastEvent);
                    break;
            }
        } catch (Exception ignore) {
        }
        return true;
    });

    private void notifyEventReceiver(EventDataInfo _eventData) {
        switch (_eventData.getEventCode()) {
            case EVENT_APP_TIMEZONE_CODE:
                view.onTimeZoneChange(_eventData);
                break;
            /**
             * In Chat
             */
            case EVENT_MESSAGE_CODE:
                view.onMessageEventReceived(_eventData);
                break;
            case EVENT_GROUP_CHAT_CODE:
                view.onGroupChatEventReceived(_eventData);
                break;
            /**
             * Volume In App
             */
            case EVENT_NOTIFICATION_VOLUME_CODE:
            case EVENT_NOTIFICATION_CALL_CODE:
            case EVENT_NOTIFICATION_MESSAGE_CODE:
                view.onNotificationChanged(_eventData);
                break;
            case EVENT_TIMEFORMAT_CODE:
                setTimeFormat(_eventData.getContent());
                break;
        }
    }

    private void setTimeFormat(String timeFormatJson) {
        Timber.i("timeFormatJson = " + timeFormatJson);
        String timeFormat = "24";
        try {
            JSONObject jsonObject = new JSONObject(timeFormatJson);
            timeFormat = jsonObject.getString("timeFormat");
        } catch (Exception e) {
            Timber.e(" setTimeFormat e = " + e);
        }
        Timber.i("timeFormat = " + timeFormat);
        if (mContext != null)
            Settings.System.putString(mContext.getContentResolver(), Settings.System.TIME_12_24, timeFormat);
    }

    @Override
    public void onDeviceStatusActionReceived(Intent intent) {
        if (intent == null)
            return;
        switch (intent.getAction()) {
            case Intent.ACTION_POWER_CONNECTED:
                Timber.e("Intent.ACTION_POWER_CONNECTED");
                CombineObjectConstance.getInstance().setBatteryStatusCharging(true);
                view.powerConnected();
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                Timber.e("Intent.ACTION_POWER_DISCONNECTED");
                CombineObjectConstance.getInstance().setBatteryStatusCharging(false);
                view.powerUnConnected();
                break;
            case Intent.ACTION_BATTERY_CHANGED:
                /*int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                batteryPercent = level;*/
                Timber.e("Intent.ACTION_BATTERY_CHANGED");
                int chargeState = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                switch (chargeState) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        batteryChangeInfo();
                        view.powerConnected();
                        break;
                    default:
                        batteryChangeInfoAndResource();
                }
                checkStatusBattery(intent);
                break;
            case Intent.ACTION_BATTERY_OKAY:
                Timber.e("Intent.ACTION_BATTERY_OKAY");
                view.batteryOkay(intent);
                break;
            case Intent.ACTION_BATTERY_LOW:
                Timber.e("Intent.ACTION_BATTERY_LOW");
                view.batteryLow(intent);
                break;
        }
    }

    private int getBatterySystem() {
        if (mContext == null)
            return 0;
        BatteryManager bm = (BatteryManager) mContext.getSystemService(BATTERY_SERVICE);
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    private void batteryChangeInfo() {
        _handler.sendMessage(_handler.obtainMessage(BATTERY_PERCENT_MESSAGE, getBatterySystem()));
    }

    private void batteryChangeInfoAndResource() {
        _handler.sendMessage(_handler.obtainMessage(BATTERY_MESSAGE, getBatterySystem()));
    }

    private void checkStatusBattery(Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        if (status == BatteryManager.BATTERY_STATUS_FULL)
            view.batteryFully();
    }

    @Override
    public void onSignalChange(int signal) {
        _handler.sendMessage(_handler.obtainMessage(SIGNAL_MESSAGE, signal));
    }

    @Override
    public void onNoSimCardPlugin() {
        _handler.sendMessage(_handler.obtainMessage(NO_SIM_MESSAGE));
    }

    private int batteryConvertToResource(long battery) {
        Timber.i("Battery : " + battery);
        if (battery <= 5) {
            return R.drawable.battery_lower;
        } else if (battery <= 30) {
            return R.drawable.battery_low;
        } else if (battery <= 50) {
            return R.drawable.battery_middle;
        } else if (battery <= 75) {
            return R.drawable.battery_middle_high;
        } else {
            return R.drawable.battery_fully;
        }
    }

    private int signalConvertToResource(int signal) {
        Timber.i("Signal : " + signal);
        if (signal == CellSignalStrength.SIGNAL_STRENGTH_GREAT) {
            return R.drawable.signal_4;
        } else if (signal == CellSignalStrength.SIGNAL_STRENGTH_GOOD) {
            return R.drawable.signal_3;
        } else if (signal == CellSignalStrength.SIGNAL_STRENGTH_MODERATE) {
            return R.drawable.signal_2;
        } else if (signal == CellSignalStrength.SIGNAL_STRENGTH_POOR) {
            return R.drawable.signal_1;
        } else {
            return R.drawable.signal_no_service;
        }
    }

}
