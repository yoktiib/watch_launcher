package com.pomohouse.launcher.manager.event;

import java.util.ArrayList;

/**
 * Created by Admin on 9/12/16 AD.
 */
public class EventPrefModel {
    public ArrayList<String> listEvent = new ArrayList<>();

    public EventPrefModel() {
    }

    public ArrayList<String> getListEvent() {
        return listEvent;
    }

    public void setListEvent(ArrayList<String> listEvent) {
        this.listEvent = listEvent;
    }
/*
    public String getEventListToData() {
        String eventList = "";
        if (listEvent != null) {
            for (String event : listEvent)
                eventList
                return
        } return "";
    }*/
}
