package com.pomohouse.library.manager.bus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

import java.util.ArrayList;

/**
 * Created by nuuneoi on 12/6/14 AD.
 */
public class BusProvider extends Bus {
    private ArrayList<Object> registeredObjects = new ArrayList<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private static BusProvider instance;

    public static BusProvider getInstance() {
        if (instance == null)
            instance = new BusProvider();
        return instance;
    }

    @Override
    public void register(Object object) {
        if (!registeredObjects.contains(object)) {
            registeredObjects.add(object);
            super.register(object);
        }
    }

    @Override
    public void unregister(Object object) {
        if (registeredObjects.contains(object)) {
            registeredObjects.remove(object);
            super.unregister(object);
        }
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mHandler.post(() -> BusProvider.super.post(event));
        }
    }
}
