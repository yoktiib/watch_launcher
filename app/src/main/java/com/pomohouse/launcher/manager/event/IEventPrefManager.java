package com.pomohouse.launcher.manager.event;

/**
 * Created by Admin on 9/5/16 AD.
 */
public interface IEventPrefManager {

    void addEvent(EventPrefModel eventPref);

    EventPrefModel getEvent();

    void removeEvent();
}
