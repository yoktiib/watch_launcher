package com.pomohouse.library.manager.shared.controls;

import com.pomohouse.library.manager.shared.WearerModelPref;

/**
 * Created by Admin on 4/27/2016 AD.
 */
public interface ISharedWearerInfo {

    void addWearerInfo(WearerModelPref wearerModel);

    WearerModelPref getWearerInfo();

    void removeWearerInfo();

}