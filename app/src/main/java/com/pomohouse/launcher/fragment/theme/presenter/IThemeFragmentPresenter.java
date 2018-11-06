package com.pomohouse.launcher.fragment.theme.presenter;

import com.pomohouse.launcher.manager.notifications.INotificationManager;
import com.pomohouse.library.base.interfaces.presenter.IBaseRequestStatePresenter;
import com.pomohouse.launcher.models.EventDataInfo;

/**
 * Created by Admin on 11/7/2016 AD.
 */

public interface IThemeFragmentPresenter extends IBaseRequestStatePresenter{
   void eventReceived(EventDataInfo event);
   void onCheckNotificationIcon(INotificationManager notificationManager);
}
