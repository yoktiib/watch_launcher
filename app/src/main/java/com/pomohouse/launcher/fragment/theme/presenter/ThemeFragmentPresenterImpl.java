package com.pomohouse.launcher.fragment.theme.presenter;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.pomohouse.launcher.fragment.theme.IThemeFragmentView;
import com.pomohouse.launcher.fragment.theme.interactor.IThemeFragmentInteractor;
import com.pomohouse.launcher.manager.notifications.INotificationManager;
import com.pomohouse.launcher.manager.notifications.NotificationPrefModel;
import com.pomohouse.launcher.utils.EventConstant;
import com.pomohouse.library.base.BaseRetrofitPresenter;
import com.pomohouse.launcher.models.events.NotificationMainIconDao;
import com.pomohouse.launcher.models.EventDataInfo;

import timber.log.Timber;

import static com.pomohouse.launcher.utils.EventConstant.EventChat.EVENT_GROUP_CHAT_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventChat.EVENT_MESSAGE_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventDeviceInfo.EVENT_APP_TIMEZONE_CODE;

/**
 * Created by Admin on 11/7/2016 AD.
 */

public class ThemeFragmentPresenterImpl extends BaseRetrofitPresenter implements IThemeFragmentPresenter {
    private static final int EVENT_RECEIVED = 200;
    private IThemeFragmentView view;
    private IThemeFragmentInteractor interactor;

    public ThemeFragmentPresenterImpl(IThemeFragmentView view, IThemeFragmentInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void onInitial(Object... param) {
        super.onInitial(param);
    }

    @Override
    public void onCheckNotificationIcon(INotificationManager notificationManager) {
        try {
            NotificationPrefModel notification = notificationManager.getNotification();
            if (notification != null) {
                view.notificationMessage(notification.isHaveGroupChat() || notification.isHaveMessage());
                view.notificationCall(notification.isHaveMissCall());
                view.notificationVolumeMute(notification.isHaveMute() || notification.isHaveSilent());
            }
        } catch (Exception ignore) {

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void eventReceived(EventDataInfo event) {
        _handlerEvent.sendMessage(_handlerEvent.obtainMessage(EVENT_RECEIVED, event));
    }

    private final Handler _handlerEvent = new Handler(msg -> {
        if (msg.what == EVENT_RECEIVED) {
            EventDataInfo eventDataInfo = (EventDataInfo) msg.obj;
            if (eventDataInfo != null) {
                Timber.e(eventDataInfo.getContent() + " : " + eventDataInfo.getEventCode());
                switch (eventDataInfo.getEventCode()) {
                    case EVENT_MESSAGE_CODE:
                    case EVENT_GROUP_CHAT_CODE:
                        view.notificationMessage(true);
                        break;
                    case EventConstant.EventSetting.EVENT_NOTIFICATION_VOLUME_CODE:
                        NotificationMainIconDao statusVolume = new Gson().fromJson(eventDataInfo.getContent(), NotificationMainIconDao.class);
                        view.notificationVolumeMute(statusVolume.isShow());
                        break;
                    case EventConstant.EventSetting.EVENT_NOTIFICATION_MESSAGE_CODE:
                        NotificationMainIconDao statusMessage = new Gson().fromJson(eventDataInfo.getContent(), NotificationMainIconDao.class);
                        view.notificationMessage(statusMessage.isShow());
                        break;
                    case EventConstant.EventSetting.EVENT_NOTIFICATION_CALL_CODE:
                        NotificationMainIconDao statusCall = new Gson().fromJson(eventDataInfo.getContent(), NotificationMainIconDao.class);
                        view.notificationCall(statusCall.isShow());
                        break;
                    case EVENT_APP_TIMEZONE_CODE:
                        view.updateTimezone();
                        break;
                }
            }
        }
        return true;
    });
}
