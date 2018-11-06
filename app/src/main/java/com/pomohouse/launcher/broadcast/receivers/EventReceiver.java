package com.pomohouse.launcher.broadcast.receivers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.gson.Gson;
import com.pomohouse.launcher.broadcast.BaseBroadcast;
import com.pomohouse.launcher.broadcast.callback.EventToContactListener;
import com.pomohouse.launcher.broadcast.callback.EventToLauncherListener;
import com.pomohouse.launcher.broadcast.callback.EventToLockScreenListener;
import com.pomohouse.launcher.broadcast.callback.EventToMainListener;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.launcher.utils.EventConstant;
import com.pomohouse.library.networks.MetaDataNetwork;

import timber.log.Timber;

import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_EXTRA;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_STATUS_EXTRA;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_LOCK_SCREEN_DEFAULT_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_LOCK_SCREEN_IN_CLASS_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_UNLOCK_SCREEN_DEFAULT_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_UNLOCK_SCREEN_DEFAULT_TO_LAUNCHER_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_UNLOCK_SCREEN_IN_CLASS_CODE;


/**
 * Created by Admin on 8/24/16 AD.
 */
public class EventReceiver extends BaseBroadcast {
    private EventToMainListener mEventToMainListener;
    private EventToLauncherListener mEventToLauncherListener;
    private EventToContactListener mEventToContactListener;
    private EventToLockScreenListener mEventToLockScreenListener;
    private static EventReceiver receiver;

    public static EventReceiver getInstance() {
        if (receiver == null)
            receiver = new EventReceiver();
        return receiver;
    }

    public void startEventReceiver(Context mContext) {
        if (mContext == null)
            return;
        final IntentFilter filter = new IntentFilter();
        filter.addAction(SEND_EVENT_UPDATE_INTENT);
        filter.addAction(RECEIVE_FROM_OTHER_APP);
        filter.addAction(SEND_EVENT_LOCK_IN_CLASS_INTENT);
        filter.addAction(SEND_EVENT_LOCK_DEFAULT_INTENT);
        filter.addAction(SEND_EVENT_UNLOCK_IN_CLASS_INTENT);
        filter.addAction(SEND_EVENT_UNLOCK_DEFAULT_INTENT);
        filter.addAction(SEND_EVENT_UNLOCK_DEFAULT_TO_LAUNCHER_INTENT);
        if (receiver != null)
            mContext.registerReceiver(receiver, filter);
    }

    public void initEventMainListener(EventToMainListener listener) {
        Timber.e("Init Main Event Listener");
        this.mEventToMainListener = listener;
    }

    public void initEventLauncherListener(EventToLauncherListener listener) {
        Timber.e("Init Launcher Event Listener");
        this.mEventToLauncherListener = listener;
    }

    public void initEventContactListener(EventToContactListener listener) {
        Timber.e("Init Contact Event Listener");
        this.mEventToContactListener = listener;
    }

    public void initEventToLockScreenListener(EventToLockScreenListener mEventToLockScreenListener) {
        Timber.e("Init LockScreen Event Listener");
        this.mEventToLockScreenListener = mEventToLockScreenListener;
    }

    public boolean stopEventService(Context mContext) {
        if (mContext == null)
            return false;
        if (receiver != null)
            mContext.unregisterReceiver(receiver);
        return true;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        try {
            if (intent.getAction().equals(SEND_EVENT_UPDATE_INTENT)) {
                MetaDataNetwork rc = (MetaDataNetwork) intent.getSerializableExtra(EVENT_STATUS_EXTRA);
                if (rc.getResCode() != 0) {
                    Timber.d("Received event Service error : " + rc.getResDesc());
                    return;
                }
                EventDataInfo eventData = (EventDataInfo) intent.getSerializableExtra(EVENT_EXTRA);
                Timber.d("Received event Service : " + rc.getResCode() + " : " + eventData.getEventCode());
                switch (eventData.getEventCode()) {
                    /**
                     * Event To Launcher
                     * EVENT_APP_TIMEZONE_CODE
                     * EVENT_MESSAGE_CODE
                     * EVENT_GROUP_CHAT_CODE
                     * EVENT_NOTIFICATION_VOLUME_CODE
                     * EVENT_NETWORK_CODE
                     * EVENT_NOTIFICATION_MESSAGE_CODE
                     */
                    case EventConstant.EventDeviceInfo.EVENT_APP_TIMEZONE_CODE:
                    case EventConstant.EventChat.EVENT_MESSAGE_CODE:
                    case EventConstant.EventChat.EVENT_GROUP_CHAT_CODE:
                    case EventConstant.EventSetting.EVENT_NETWORK_CODE:
                    case EventConstant.EventSetting.EVENT_NOTIFICATION_VOLUME_CODE:
                    case EventConstant.EventSetting.EVENT_NOTIFICATION_MESSAGE_CODE:
                    case EventConstant.EventSetting.EVENT_NOTIFICATION_CALL_CODE:
                    case EventConstant.EventDeviceInfo.EVENT_TIMEFORMAT_CODE:
                        if (mEventToMainListener != null)
                            mEventToMainListener.eventReceived(eventData);
                        break;
                    /**
                     * Event To Contact
                     */
                    case EventConstant.EventContact.EVENT_UPDATE_LOCAL_CONTACT:
                        if (mEventToContactListener != null)
                            mEventToContactListener.eventReceived(eventData);
                        break;
                    /**
                     * Event To LockScreen
                     */
                    case EventConstant.EventLocal.EVENT_SOS_CODE:
                    case EventConstant.EventLocal.EVENT_ALARM_CODE:
                    case EventConstant.EventLocal.EVENT_BATTERY_CHARGING_CODE:
                        /**
                         * Notification
                         */
                    case EventConstant.EventLocal.EVENT_LOCK_SCREEN_NOTIFICATION_MESSAGE_CODE:
                    case EventConstant.EventLocal.EVENT_LOCK_SCREEN_NOTIFICATION_CALL_CODE:
                    case EventConstant.EventLocal.EVENT_LOCK_SCREEN_NOTIFICATION_MUTE_CODE:
                        if (mEventToLockScreenListener != null)
                            mEventToLockScreenListener.eventReceived(eventData);
                        break;
                    /**
                     * Event To Main
                     */
                    default:
                        if (mEventToLauncherListener != null)
                            mEventToLauncherListener.eventReceived(eventData);
                        break;
                }
            } else if (intent.getAction().equals(RECEIVE_FROM_OTHER_APP)) {
                String eventData = intent.getStringExtra(EVENT_EXTRA);
                EventDataInfo eventFrom = new Gson().fromJson(eventData, EventDataInfo.class);
                if (eventFrom != null && mEventToLauncherListener != null)
                    mEventToLauncherListener.eventReceived(eventFrom);
            } else if (intent.getAction().equals(SEND_EVENT_LOCK_IN_CLASS_INTENT)) {
                sendToManagingLockScreen(EVENT_LOCK_SCREEN_IN_CLASS_CODE);
            } else if (intent.getAction().equals(SEND_EVENT_LOCK_DEFAULT_INTENT)) {
                sendToManagingLockScreen(EVENT_LOCK_SCREEN_DEFAULT_CODE);
            } else if (intent.getAction().equals(SEND_EVENT_UNLOCK_IN_CLASS_INTENT)) {
                sendToManagingLockScreen(EVENT_UNLOCK_SCREEN_IN_CLASS_CODE);
            } else if (intent.getAction().equals(SEND_EVENT_UNLOCK_DEFAULT_INTENT)) {
                sendToManagingLockScreen(EVENT_UNLOCK_SCREEN_DEFAULT_CODE);
            } else if (intent.getAction().equals(SEND_EVENT_UNLOCK_DEFAULT_TO_LAUNCHER_INTENT)) {
                EventDataInfo eventContent = new EventDataInfo();
                eventContent.setEventCode(EVENT_UNLOCK_SCREEN_DEFAULT_TO_LAUNCHER_CODE);
                if (mEventToLauncherListener != null)
                    mEventToLauncherListener.eventReceived(eventContent);
            }
        } catch (Exception ignore) {
        }
    }

    public void sendToManagingLockScreen(int eventCode) {
        if (mEventToLockScreenListener == null)
            return;
        EventDataInfo eventContent = new EventDataInfo();
        eventContent.setEventCode(eventCode);
        mEventToLockScreenListener.eventReceived(eventContent);
    }

    public void removeContactSyncListener() {
        Timber.e("Remove ContactSync Listener");
        this.mEventToContactListener = null;
    }

    public void destroyEventListener() {
        Timber.e("Destroy EventListener");
        this.mEventToMainListener = null;
        this.mEventToContactListener = null;
        this.mEventToLauncherListener = null;
        this.mEventToLockScreenListener = null;
    }
}
