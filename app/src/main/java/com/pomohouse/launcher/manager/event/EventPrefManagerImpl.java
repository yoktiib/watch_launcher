package com.pomohouse.launcher.manager.event;

import android.content.Context;

import com.google.gson.Gson;
import com.pomohouse.library.manager.shared.AbstractSharedPreferences;

/**
 * Created by Admin on 9/5/16 AD.
 */
public class EventPrefManagerImpl extends AbstractSharedPreferences implements IEventPrefManager {
    private final String EVENT = "KEY_EVENT";

    public EventPrefManagerImpl(Context mContext) {
        super(mContext);
    }

    @Override
    public void addEvent(EventPrefModel eventPref) {
        writeString(EVENT, new Gson().toJson(eventPref));
    }

    @Override
    public EventPrefModel getEvent() {
        EventPrefModel fitnessPref = new Gson().fromJson(readString(EVENT), EventPrefModel.class);
        if (fitnessPref == null)
            fitnessPref = new EventPrefModel();
        return fitnessPref;
    }

    @Override
    public void removeEvent() {
        removeKey(EVENT);
    }
}
