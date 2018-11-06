package com.pomohouse.launcher.manager.notifications;

/**
 * Created by Admin on 9/5/16 AD.
 */
public interface INotificationManager {

    void addNotification(NotificationPrefModel wearerModel);

    NotificationPrefModel getNotification();

    void removeNotification();
}
