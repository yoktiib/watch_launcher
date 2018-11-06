package com.pomohouse.launcher.manager.notifications;

import android.content.Context;

import com.google.gson.Gson;
import com.pomohouse.launcher.manager.DataManagerConstant;
import com.pomohouse.library.manager.shared.AbstractSharedPreferences;

/**
 * Created by Admin on 9/5/16 AD.
 */
public class NotificationPrefManager extends AbstractSharedPreferences implements INotificationManager {
    private final String KEY_NOTIFICATION = DataManagerConstant.KEY_NOTIFICATION;

    public NotificationPrefManager(Context mContext) {
        super(mContext);
    }

    @Override
    public void addNotification(NotificationPrefModel backgroundPref) {
        writeString(KEY_NOTIFICATION, new Gson().toJson(backgroundPref));
    }

    @Override
    public NotificationPrefModel getNotification() {
        NotificationPrefModel powerSavingPrefModel = new Gson().fromJson(readString(KEY_NOTIFICATION), NotificationPrefModel.class);
        if (powerSavingPrefModel == null)
            addNotification(powerSavingPrefModel = new NotificationPrefModel());
        return powerSavingPrefModel;
    }

    @Override
    public void removeNotification() {
        removeKey(KEY_NOTIFICATION);
    }
}
