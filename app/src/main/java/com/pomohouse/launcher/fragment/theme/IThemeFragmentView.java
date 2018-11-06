package com.pomohouse.launcher.fragment.theme;

import com.pomohouse.library.base.interfaces.IBaseRequestView;

/**
 * Created by Admin on 11/7/2016 AD.
 */

public interface IThemeFragmentView extends IBaseRequestView {
/*

    void onHideMuteVolumeNotification();

    void onShowMessageNotification();

    void onShowMissCallNotification();

    void onHideMissCallNotification();

    void onShowMuteVolumeNotification();

    void onHideMessageNotification();
*/

    void updateTimezone();

    void notificationMessage(boolean b);

    void notificationCall(boolean haveMissCall);

    void notificationVolumeMute(boolean b);
}
