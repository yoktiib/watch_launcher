package com.pomohouse.launcher.models.events;

/**
 * Created by Admin on 5/29/2017 AD.
 */

public class BatteryChargerEvent {

    private boolean isCharger;

    public boolean isCharger() {
        return isCharger;
    }

    public void setCharger(boolean charger) {
        isCharger = charger;
    }
}
