package com.pomohouse.launcher.broadcast.receivers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.pomohouse.launcher.broadcast.BaseBroadcast;
import com.pomohouse.launcher.broadcast.callback.DevicePowerListener;
import com.pomohouse.launcher.broadcast.callback.DeviceStatusListener;
import com.pomohouse.launcher.broadcast.callback.ScreenOnListener;
import com.pomohouse.launcher.broadcast.callback.TimeTickChangedListener;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;

import timber.log.Timber;


/**
 * Lock Screen Receiver
 *
 * @author Andy
 */
public class DeviceActionReceiver extends BaseBroadcast {
    private DeviceStatusListener mDeviceStatusListener;
    private DevicePowerListener mDevicePowerListener;
    private static DeviceActionReceiver receiver;
    private TimeTickChangedListener mLauncherTimeTickChangedListener;
    private TimeTickChangedListener mThemeTimeTickChangedListener;
    private TimeTickChangedListener mLockScreenTimeTickChangedListener;
    private ScreenOnListener screenOnListener;


    public void setLockScreenScreenOnChangedListener(ScreenOnListener screenOnListener) {
        this.screenOnListener = screenOnListener;
    }

    public void setLauncherTimeTickChangedListener(TimeTickChangedListener mLauncherTimeTickChangedListener) {
        this.mLauncherTimeTickChangedListener = mLauncherTimeTickChangedListener;
    }

    public void setThemeTimeTickChangedListener(TimeTickChangedListener mThemeTimeTickChangedListener) {
        this.mThemeTimeTickChangedListener = mThemeTimeTickChangedListener;
    }

    public void setLockScreenTimeTickChangedListener(TimeTickChangedListener mLockScreenTimeTickChangedListener) {
        this.mLockScreenTimeTickChangedListener = mLockScreenTimeTickChangedListener;
    }

    public void removeLockScreenTimeTickChangedListener() {
        this.mLockScreenTimeTickChangedListener = null;
    }

    public void removeThemeTimeTickChangedListener() {
        this.mThemeTimeTickChangedListener = null;
    }

    public void removeLauncherTimeTickChangedListener() {
        this.mLauncherTimeTickChangedListener = null;
    }

    public void removeLockScreenScreenOnListener() {
        this.screenOnListener = null;
    }

    public static DeviceActionReceiver getInstance() {
        if (receiver == null) receiver = new DeviceActionReceiver();
        return receiver;
    }

    public void startDeviceActionReceiver(Context mContext) {
        if (mContext == null) return;
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_REBOOT);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(SEND_EVENT_TIME_UPDATE_INTENT);
        mContext.registerReceiver(receiver, filter);
    }

    public boolean stopDeviceActionService(Context mContext) {
        if (mContext == null) return false;
        if (receiver != null) mContext.unregisterReceiver(receiver);
        return true;
    }

    public void initDeviceActionListener(DeviceStatusListener listener) {
        this.mDeviceStatusListener = listener;
    }

    public void initDevicePowerActionListener(DevicePowerListener listener) {
        this.mDevicePowerListener = listener;
    }

    /*public void addTimeTickChangedListener(TimeTickChangedListener _mTimeTickChangedListener) {
        Timber.e("Add Time Tick Listener");
        this.mTimeTickChangedListener.add(_mTimeTickChangedListener);
    }

    public void removeTimeTickChangedListener(TimeTickChangedListener mTimeTickChangedListener) {
        Timber.e("Remove Time Tick Listener");
        this.mTimeTickChangedListener.remove(mTimeTickChangedListener);
    }*/

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            try {
                switch (intent.getAction()) {
                    /**
                     * Power
                     */
                    case Intent.ACTION_SCREEN_OFF:
                        TCPSocketServiceProvider.getInstance().screenOff();
                    case Intent.ACTION_REBOOT:
                    case Intent.ACTION_SHUTDOWN:
                    case Intent.ACTION_BOOT_COMPLETED:
                    case Intent.ACTION_TIMEZONE_CHANGED:
                    case Intent.ACTION_TIME_CHANGED:
                        sendToLauncherListener(intent);
                        break;
/*
                sendToLauncherListener(intent);
                //onRunLockScreen(context);
                //new Handler().postDelayed(() -> , 500);
                break;
            case SEND_EVENT_TIME_UPDATE_INTENT:
                sendToLauncherListener(intent);
                break;
*/
                    case Intent.ACTION_POWER_DISCONNECTED:
                    case Intent.ACTION_POWER_CONNECTED:
                        sendMainFaceListener(intent);
                        sendToLauncherListener(intent);
                        break;
                    case Intent.ACTION_BATTERY_CHANGED:
                    case Intent.ACTION_BATTERY_LOW:
                    case Intent.ACTION_BATTERY_OKAY:
                        sendMainFaceListener(intent);
                        break;
                    case Intent.ACTION_SCREEN_ON:
                        TCPSocketServiceProvider.getInstance().screenOn();
                        if (screenOnListener != null) screenOnListener.onScreenOn();
                        break;
                    case Intent.ACTION_TIME_TICK:
                        Timber.e("Time Tick Size => " + (mLauncherTimeTickChangedListener != null) + ":" + (mThemeTimeTickChangedListener != null) + ":" + (mLockScreenTimeTickChangedListener != null));
                        if (mLauncherTimeTickChangedListener != null)
                            mLauncherTimeTickChangedListener.onTimeChanged();
                        if (mThemeTimeTickChangedListener != null)
                            mThemeTimeTickChangedListener.onTimeChanged();
                        if (mLockScreenTimeTickChangedListener != null)
                            mLockScreenTimeTickChangedListener.onTimeChanged();
                        break;
                }
            } catch (NullPointerException ignored) {
            }
        }
    }

    public void sendMainFaceListener(Intent intent) {
        if (mDeviceStatusListener != null) mDeviceStatusListener.onDeviceAction(intent);
    }

    public void sendToLauncherListener(Intent intent) {
        if (mDevicePowerListener != null) mDevicePowerListener.onDeviceAction(intent);
    }

    public void destroyEventListener() {
        this.mDevicePowerListener = null;
        this.mDeviceStatusListener = null;
        this.mLauncherTimeTickChangedListener = null;
        this.mThemeTimeTickChangedListener = null;
        this.mLockScreenTimeTickChangedListener = null;
    }
}
