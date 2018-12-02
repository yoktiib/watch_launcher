package com.pomohouse.launcher.fragment.settings.interactor;

import com.pomohouse.launcher.api.WatchService;
import com.pomohouse.library.base.BaseInteractor;
import com.pomohouse.library.networks.ServiceApiGenerator;

/**
 * Created by Admin on 8/18/16 AD.
 */
public class SettingInteractorImpl extends BaseInteractor implements ISettingInteractor {
    WatchService settingService;

    public SettingInteractorImpl() {
        settingService = ServiceApiGenerator.getInstance().createService(WatchService.class);
    }
}
